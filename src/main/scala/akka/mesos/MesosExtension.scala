package akka.mesos

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.libprocess.LibProcessManager._
import akka.libprocess.{ LibProcess, LibProcessMessage, PID }
import akka.mesos.protos._
import akka.mesos.protos.internal._
import akka.pattern.pipe
import akka.util.{ ByteString, Timeout }
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.collection.immutable.Seq
import scala.concurrent.duration._
import scala.util.control.NonFatal

object Mesos extends ExtensionId[MesosExtension] with ExtensionIdProvider {
  override def lookup(): ExtensionId[_ <: Extension] = this

  override def createExtension(system: ExtendedActorSystem): MesosExtension =
    new MesosExtension(system)
}

class MesosExtension(system: ExtendedActorSystem) extends Extension {
  def registerFramework(master: PID, framework: FrameworkInfo, schedulerCreator: SchedulerDriver => Scheduler): ActorRef = {
    system.actorOf(Props(classOf[MesosFramework], master, framework, schedulerCreator))
  }
}

class MesosFramework(master: PID, framework: FrameworkInfo, schedulerCreator: SchedulerDriver => Scheduler) extends Actor with Stash with ActorLogging {
  import akka.mesos.MesosFramework._
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
      LibProcess(context.system).remoteRef(master).map(MasterRef) pipeTo self

    case MasterRef(ref) =>
      val msg = RegisterFrameworkMessage(framework)
      ref ! LibProcessMessage(framework.name, msg)
      val timeout: Timeout = context.system.settings.config.getDuration("akka.libprocess.timeout", TimeUnit.MILLISECONDS).millis
      scheduler = context.actorOf(Props(classOf[SchedulerActor], framework, self, ref, timeout, schedulerCreator))
      context.become(ready)
      unstashAll()

    case _ => stash()
  }

  def ready: Receive = {
    case x => scheduler forward x
  }
}

object MesosFramework {
  case class MasterRef(ref: ActorRef)
}

class SchedulerActor(
    frameworkInfo: FrameworkInfo,
    framework: ActorRef,
    master: ActorRef,
    timeout: Timeout,
    schedulerCreator: SchedulerDriver => Scheduler) extends Actor with ActorLogging {

  var scheduler: Scheduler = _

  def receive = {
    case FrameworkRegisteredMessage(frameworkId, masterInfo) =>
      scheduler = schedulerCreator(
        new SchedulerDriver(
          frameworkInfo,
          frameworkId,
          framework,
          master)(context.dispatcher, timeout))

      scheduler.registered(frameworkId, masterInfo)
      context.become(ready)
  }

  def ready: Receive = {
    case ResourceOffersMessage(offers, _) => scheduler.resourceOffers(offers)
    case FrameworkErrorMessage(message)   => scheduler.error(message)
    case ExitedExecutorMessage(slaveId, _, executorId, status) =>
      scheduler.executorLost(executorId, slaveId, status)

    case RescindResourceOfferMessage(offerId) => scheduler.offerRescinded(offerId)
    case StatusUpdateMessage(statusUpdate, _) => scheduler.statusUpdate(statusUpdate.status)
    case ExecutorToFrameworkMessage(slaveId, _, executorId, data) =>
      scheduler.frameworkMessage(executorId, slaveId, data)

    case LostSlaveMessage(slaveId) => scheduler.slaveLost(slaveId)
    case x                         => log.warning(s"Received unexpected message: $x")
  }
}

class MyScheduler(_driver: SchedulerDriver) extends Scheduler(_driver) {

  val log = LoggerFactory.getLogger(getClass)
  var taskCounter: Int = 0

  override def registered(frameworkId: FrameworkID, masterInfo: MasterInfo): Unit = {
    log.info(s"Framework $frameworkId registered with master ${masterInfo.id}")
  }

  override def offerRescinded(offerId: OfferID): Unit = {

  }

  override def disconnected(): Unit = {

  }

  override def reregistered(masterInfo: MasterInfo): Unit = {

  }

  override def slaveLost(slaveId: SlaveID): Unit = {

  }

  override def error(message: String): Unit = {}

  override def frameworkMessage(executorId: ExecutorID, slaveId: SlaveID, data: ByteString): Unit = {}

  override def statusUpdate(status: TaskStatus): Unit = {
    log.info(s"Task ${status.taskId.value} is now ${status.state}")
  }

  override def resourceOffers(offers: Seq[Offer]): Unit = {
    offers foreach { offer =>
      log.info(s"Starting sleep task with offer: ${offer.id}")
      val task = TaskInfo(
        name = "sleep",
        taskId = TaskID(s"sleep-$taskCounter"),
        slaveId = offer.slaveId,
        offer.resources.filter(_.tpe != ResourceType.PORTS),
        command = Some(
          CommandInfo(
            value = "sleep 1",
            user = Some("root")
          )
        )
      )

      taskCounter += 1
      driver.launchTasks(Seq(task -> offer.id))
    }
  }

  override def executorLost(executorId: ExecutorID, slaveId: SlaveID, status: Int): Unit = {}
}

object Main extends App {
  import akka.actor._

  val config = ConfigFactory.load()

  val system = ActorSystem("Mesos", config)

  Mesos(system).registerFramework(PID("192.168.178.23", 5050, "master"), FrameworkInfo("foo", "corpsi"), driver => new MyScheduler(driver))
}
