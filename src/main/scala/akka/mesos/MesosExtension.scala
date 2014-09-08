package akka.mesos

import akka.mesos.protos.{ DeclineResourceOfferMessage, FrameworkID, FrameworkInfo }
import akka.actor.{ ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import akka.libprocess.{ LibProcess, PID, LibProcessMessage }
import akka.libprocess.LibProcessManager._
import akka.actor._
import akka.mesos.protos.internal._

import scala.util.control.NonFatal

object Mesos extends ExtensionId[MesosExtension] with ExtensionIdProvider {
  override def lookup(): ExtensionId[_ <: Extension] = this

  override def createExtension(system: ExtendedActorSystem): MesosExtension =
    new MesosExtension(system)
}

class MesosExtension(system: ExtendedActorSystem) extends Extension {

  def registerFramework(master: PID, framework: FrameworkInfo, schedulerProps: (String, FrameworkID) => Props): ActorRef = {
    system.actorOf(Props(classOf[MesosFramework], master, framework, schedulerProps))
  }
}

class MesosFramework(master: PID, framework: FrameworkInfo, schedulerProps: (String, FrameworkID) => Props) extends Actor with Stash with ActorLogging {
  import context.dispatcher

  var scheduler: ActorRef = _

  override def preStart(): Unit = {
    LibProcess(context.system).manager ! Register(framework.name, self)
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case NonFatal(_) => SupervisorStrategy.Restart
  }

  def receive = {
    case Registered(framework.`name`) =>
      LibProcess(context.system).remoteRef(master) onSuccess {
        case ref =>
          val msg = RegisterFrameworkMessage(framework)
          ref ! LibProcessMessage(framework.name, msg)
      }

    case FrameworkRegisteredMessage(frameworkId, _) =>
      log.info(s"Successfully registered framework '${frameworkId.value}'")
      scheduler = context.actorOf(schedulerProps(framework.name, frameworkId))
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

  Mesos(system).registerFramework(PID("127.0.0.1", 5050, "master"), FrameworkInfo("foo", "corpsi"), (frameworkName, frameworkId) => Props(classOf[SchedulerActor], frameworkName, frameworkId))
}

class SchedulerActor(frameworkName: String, frameworkId: FrameworkID) extends Actor with ActorLogging {
  def receive = {
    case offers: ResourceOffersMessage =>
      offers.offers foreach { offer =>
        log.info(s"Rescinding resource offer: ${offer.id} -- ${sender().path}")
        sender() ! LibProcessMessage("foo", DeclineResourceOfferMessage(frameworkId, offer.id))
      }

    case x => log.info(s"Received: $x")
  }
}

