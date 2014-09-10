package akka.mesos

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.libprocess.LibProcessManager._
import akka.libprocess.{ RemoteRefRetrievalException, LibProcess, LibProcessMessage, PID }
import akka.mesos.MesosFramework.{ MasterRef, MasterDisconnected }
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

  val connectionRetryDelay = context.system.settings.config.getDuration("akka.libprocess.retry-delay", TimeUnit.MILLISECONDS).millis

  var schedulerRef: ActorRef = _
  var masterRef: ActorRef = _
  var frameworkId: Option[FrameworkID] = None

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
      masterRef = ref
      context.watch(masterRef)
      val msg = RegisterFrameworkMessage(framework)
      masterRef ! LibProcessMessage(framework.name, msg)
      val timeout: Timeout = context.system.settings.config.getDuration("akka.libprocess.timeout", TimeUnit.MILLISECONDS).millis
      schedulerRef = context.actorOf(Props(classOf[SchedulerActor], framework, self, ref, timeout, schedulerCreator))
      context.become(ready)
      unstashAll()

    case Status.Failure(e: RemoteRefRetrievalException) =>
      log.error(e, s"Could not retrieve reference to mesos master. Trying again in ${connectionRetryDelay.toMillis}ms.")
      context.system.scheduler.scheduleOnce(connectionRetryDelay) {
        LibProcess(context.system).remoteRef(master).map(MasterRef) pipeTo self
      }

    case _ => stash()
  }

  def ready: Receive = {
    case Terminated(ref) if ref == masterRef =>
      context.unwatch(ref)
      schedulerRef ! MasterDisconnected
      scheduleReconnect()

      context.become(disconnected)

    case msg @ FrameworkRegisteredMessage(id, _) =>
      frameworkId = Some(id)
      schedulerRef forward msg

    case msg @ FrameworkReregisteredMessage(id, _) =>
      frameworkId = Some(id)
      schedulerRef forward msg

    case x: ProtoWrapper[_] => schedulerRef forward x
  }

  def disconnected: Receive = {
    case Reconnect(pid) =>
      LibProcess(context.system).remoteRef(master).map(MasterRef) pipeTo self

    case msg @ MasterRef(ref) =>
      context.watch(ref)
      masterRef = ref
      ref ! LibProcessMessage(framework.name, ReregisterFrameworkMessage(frameworkInfo = framework.copy(id = frameworkId), failover = false))
      schedulerRef ! msg
      context.become(ready)
      unstashAll()

    case Status.Failure(e) =>
      log.error(e, "Failed to reconnect to master. Trying again in ${connectionRetryDelay.toMillis}ms.")
      scheduleReconnect()

    case _ => stash()
  }

  def scheduleReconnect(): Unit = {
    context.system.scheduler.scheduleOnce(
      connectionRetryDelay,
      self,
      Reconnect(master))
  }
}

object MesosFramework {
  final case class MasterRef(ref: ActorRef)
  final case class Reconnect(pid: PID)
  case object MasterDisconnected
}

class SchedulerActor(
    frameworkInfo: FrameworkInfo,
    framework: ActorRef,
    master: ActorRef,
    timeout: Timeout,
    schedulerCreator: SchedulerDriver => Scheduler) extends Actor with ActorLogging with Stash {

  var scheduler: Scheduler = _
  var driver: SchedulerDriver = _

  def receive = {
    case FrameworkRegisteredMessage(frameworkId, masterInfo) =>
      driver = new SchedulerDriver(frameworkInfo, frameworkId, framework, master)(context.dispatcher, timeout)
      scheduler = schedulerCreator(driver)

      scheduler.registered(frameworkId, masterInfo)
      context.become(ready)
      unstashAll()

    case _ => stash()
  }

  def ready: Receive = {
    case ResourceOffersMessage(offers, _) => scheduler.resourceOffers(offers)
    case FrameworkErrorMessage(message)   => scheduler.error(message)
    case ExitedExecutorMessage(slaveId, _, executorId, status) =>
      scheduler.executorLost(executorId, slaveId, status)

    case RescindResourceOfferMessage(offerId) => scheduler.offerRescinded(offerId)
    case StatusUpdateMessage(StatusUpdate(frameworkId, status, _, uuid, _, slaveIdOpt), _) =>
      scheduler.statusUpdate(status)

      for (slaveId <- slaveIdOpt) {
        sender() ! LibProcessMessage(frameworkInfo.name, StatusUpdateAcknowledgementMessage(slaveId, frameworkId, status.taskId, uuid))
      }

    case ExecutorToFrameworkMessage(slaveId, _, executorId, data) =>
      scheduler.frameworkMessage(executorId, slaveId, data)

    case LostSlaveMessage(slaveId) => scheduler.slaveLost(slaveId)
    case MasterDisconnected =>
      scheduler.disconnected()
      context.become(disconnected)

    case FrameworkRegisteredMessage(frameworkId, masterInfo) => scheduler.registered(frameworkId, masterInfo)
    case FrameworkReregisteredMessage(frameworkId, masterInfo) => scheduler.reregistered(masterInfo)

    case x => log.warning(s"Received unexpected message: $x")
  }

  def disconnected: Receive = {
    case MasterRef(ref) =>
      driver.master = ref
      context.become(ready)
      unstashAll()

    case _ => stash()
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
    log.info(s"Lost slave: ${slaveId.value}")
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
            value = "sleep 1"
          )
        )
      )

      taskCounter += 1
      driver.launchTasks(Seq(task -> offer.id))
    }
  }

  override def executorLost(executorId: ExecutorID, slaveId: SlaveID, status: Int): Unit = {
    log.info(s"Lost executor: ${executorId.value}")
  }
}

object Main extends App {
  import akka.actor._

  val config = ConfigFactory.load()

  val system = ActorSystem("Mesos", config)

  Mesos(system).registerFramework(PID("127.0.0.1", 5050, "master"), FrameworkInfo("foo", "corpsi"), driver => new MyScheduler(driver))
}
