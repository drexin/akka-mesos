package akka.mesos

import akka.mesos.protos.FrameworkInfo
import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import akka.libprocess.{ LibProcess, PID, LibProcessMessage }
import mesos.internal.Messages._
import akka.libprocess.LibProcessManager._
import akka.actor._
import scala.collection.JavaConverters._

object Mesos extends ExtensionId[MesosExtension] with ExtensionIdProvider {
  override def lookup(): ExtensionId[_ <: Extension] = this

  override def createExtension(system: ExtendedActorSystem): MesosExtension =
    new MesosExtension(system)
}

class MesosExtension(system: ExtendedActorSystem) extends Extension {
  import system.dispatcher

  def registerFramework(master: PID, framework: FrameworkInfo, schedulerProps: Props): ActorRef = {
    system.actorOf(Props(classOf[MesosFramework], master, framework, schedulerProps))
  }
}

class MesosFramework(master: PID, framework: FrameworkInfo, schedulerProps: Props) extends Actor with Stash with ActorLogging {
  import context.dispatcher

  var scheduler: ActorRef = _

  override def preStart(): Unit = {
    LibProcess(context.system).manager ! Register(framework.name, self)
    scheduler = context.actorOf(schedulerProps)
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case e: Exception => SupervisorStrategy.Restart
  }

  def receive = {
    case Registered(framework.`name`) =>
      LibProcess(context.system).remoteRef(master) onSuccess {
        case ref =>
          val msg = RegisterFrameworkMessage
            .newBuilder()
            .setFramework(framework.toProto)
            .build()
          // TODO: create LibProcessActorRef that only accepts `LibProcessMessage`
          ref ! LibProcessMessage(framework.name, msg)
      }

    case msg: FrameworkRegisteredMessage =>
      log.info(s"Successfully registered framework '${msg.getFrameworkId.getValue}'")
      context.become(ready)
      unstashAll()

    case _ => stash()
  }

  def ready: Receive = {
    case x => scheduler forward x
  }
}

object Main extends App {
  import akka.actor._

  val system = ActorSystem("Mesos")

  Mesos(system).registerFramework(PID("127.0.0.1", 5050, "master"), FrameworkInfo("foo", "corpsi"), Props(classOf[SchedulerActor]))
}

class SchedulerActor extends Actor with ActorLogging {
  def receive = {
    case x => log.info(s"Received: $x")
  }
}

