package akka.mesos

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.libprocess.LibProcessManager.Registered
import akka.libprocess.{ LibProcess, LibProcessManager, LibProcessMessage, PID }
import akka.mesos.protos.internal._
import akka.mesos.protos.{ ProtoWrapper, FrameworkID, FrameworkInfo }
import akka.mesos.scheduler.SchedulerDriverActor
import SchedulerDriverActor.FrameworkConnected
import akka.mesos.scheduler.SchedulerPublisher.{ Disconnected, SchedulerMessage }
import akka.pattern.pipe
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.control.NonFatal
import scala.util.{ Failure, Success, Try }

private[mesos] class MesosFrameworkActor(master: => Try[PID], framework: FrameworkInfo, driverRef: ActorRef) extends Actor with Stash with ActorLogging {
  import akka.mesos.MesosFrameworkActor._
  import context.dispatcher

  val connectionRetryDelay = context.system.settings.config.getDuration("akka.libprocess.retry-delay", TimeUnit.MILLISECONDS).millis
  implicit val timeout: Timeout = context.system.settings.config.getDuration("akka.libprocess.timeout", TimeUnit.MILLISECONDS).millis

  var schedulerRef: ActorRef = _
  var masterRef: ActorRef = _
  var frameworkId: Option[FrameworkID] = framework.id
  var scheduledTask: Option[Cancellable] = None

  override def preStart(): Unit = {
    LibProcess(context.system).manager ! LibProcessManager.Register(framework.name, self)
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case NonFatal(_) => SupervisorStrategy.Restart
  }

  def receive = {
    case Registered(framework.`name`) =>
      context.become(receiveScheduler)
      unstashAll()

    case _ => stash()
  }

  def receiveScheduler: Receive = {
    case RegisterScheduler(ref) =>
      schedulerRef = ref
      scheduleConnect(0.seconds)
      context.become(connecting)
      unstashAll()

    case _ => stash()
  }

  def ready: Receive = {
    case Terminated(ref) if ref == masterRef =>
      log.warning(s"Lost connection to master, trying to reconnect in ${connectionRetryDelay.toMillis}ms.")
      context.unwatch(ref)
      scheduleConnect(connectionRetryDelay)
      context.become(connecting)

    case Deactivate =>
      driverRef ! Disconnected
      context.become(connecting)

    case x: ProtoWrapper[_] => SchedulerMessage(x).foreach(schedulerRef ! _)
  }

  def connecting: Receive = {
    case Connect =>
      log.info(s"Trying to connect to master @ $master")
      master match {
        case Success(masterPid) => LibProcess(context.system).remoteRef(masterPid).map(MasterRef) pipeTo self
        case Failure(e) =>
          log.error(e, s"Failed to retrieve master PID. Trying again in ${connectionRetryDelay.toMillis}ms.")
          scheduleConnect(connectionRetryDelay)
      }

    case msg @ MasterRef(ref) =>
      context.watch(ref)
      masterRef = ref
      self ! Register
      context.become(registering)
      unstashAll()

    case Status.Failure(e) =>
      log.error(e, s"Failed to reconnect to master. Trying again in ${connectionRetryDelay.toMillis}ms.")
      scheduleConnect(connectionRetryDelay)

    case _ => stash()
  }

  def registering: Receive = {
    case Register if frameworkId.isEmpty =>
      log.info(s"Trying to register with master @ $master")
      val msg = RegisterFrameworkMessage(framework)
      masterRef ! LibProcessMessage(framework.name, msg)
      scheduledTask = Some(context.system.scheduler.scheduleOnce(connectionRetryDelay, self, Register))

    case Register if frameworkId.isDefined =>
      log.info(s"Trying to reregister with master @ $master")
      val msg = ReregisterFrameworkMessage(frameworkInfo = framework.copy(id = frameworkId), failover = false)
      masterRef ! LibProcessMessage(framework.name, msg)
      scheduledTask = Some(context.system.scheduler.scheduleOnce(connectionRetryDelay, self, Register))

    case msg @ FrameworkRegisteredMessage(id, _) =>
      scheduledTask.foreach(_.cancel())
      frameworkId = Some(id)

      schedulerRef ! msg
      driverRef ! FrameworkConnected(masterRef, id)

      context.become(ready)
      unstashAll()

    case msg @ FrameworkReregisteredMessage(id, _) =>
      scheduledTask.foreach(_.cancel())
      frameworkId = Some(id)

      schedulerRef ! msg
      driverRef ! FrameworkConnected(masterRef, id)

      context.become(ready)
      unstashAll()

    case Terminated(ref) if ref == masterRef =>
      log.warning(s"Lost connection to master, trying to reconnect in ${connectionRetryDelay.toMillis}ms.")
      context.unwatch(ref)
      driverRef ! Disconnected
      scheduleConnect(connectionRetryDelay)
      context.become(registering)

    case _ => stash()
  }

  def scheduleConnect(delay: FiniteDuration): Unit = {
    context.system.scheduler.scheduleOnce(
      delay,
      self,
      Connect)
  }
}

object MesosFrameworkActor {
  final case class MasterRef(ref: ActorRef)
  final case class RegisterScheduler(ref: ActorRef)
  case object Connect
  case object Register
  case object Deactivate
}