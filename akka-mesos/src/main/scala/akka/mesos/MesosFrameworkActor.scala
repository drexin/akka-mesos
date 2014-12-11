package akka.mesos

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.libprocess.LibProcessManager.Registered
import akka.libprocess.{ LibProcess, LibProcessMessage, LibProcessManager, PID }
import akka.mesos.protos.internal.{ FrameworkReregisteredMessage, FrameworkRegisteredMessage, ReregisterFrameworkMessage, RegisterFrameworkMessage }
import akka.mesos.protos.{ ProtoWrapper, FrameworkID, FrameworkInfo }
import akka.mesos.scheduler.{ Scheduler, SchedulerDriver, SchedulerActor }
import akka.pattern.pipe
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{ Failure, Success, Try }
import scala.util.control.NonFatal

private[mesos] class MesosFrameworkActor(master: => Try[PID], framework: FrameworkInfo, schedulerCreator: SchedulerDriver => Scheduler) extends Actor with Stash with ActorLogging {
  import akka.mesos.MesosFrameworkActor._
  import context.dispatcher

  val connectionRetryDelay = context.system.settings.config.getDuration("akka.libprocess.retry-delay", TimeUnit.MILLISECONDS).millis

  var schedulerRef: Option[ActorRef] = None
  var masterRef: ActorRef = _
  var frameworkId: Option[FrameworkID] = None
  var scheduledTask: Option[Cancellable] = None

  override def preStart(): Unit = {
    LibProcess(context.system).manager ! LibProcessManager.Register(framework.name, self)
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case NonFatal(_) => SupervisorStrategy.Restart
  }

  def receive = {
    case Registered(framework.`name`) =>
      scheduleConnect(0.seconds)
      context.become(connecting)
      unstashAll()

    case _ => stash()
  }

  def ready: Receive = {
    case Terminated(ref) if ref == masterRef =>
      log.warning(s"Lost connection to master, trying to reconnect in ${connectionRetryDelay.toMillis}ms.")
      context.unwatch(ref)
      schedulerRef.foreach(_ ! MasterDisconnected)
      scheduleConnect(connectionRetryDelay)
      context.become(connecting)

    case Deactivate         => context.become(connecting)

    case x: ProtoWrapper[_] => schedulerRef.foreach(_ forward x)
  }

  def connecting: Receive = {
    case Connect =>
      log.info(s"Trying to connect to master @ $master")
      master match {
        case Success(masterPid) => LibProcess(context.system).remoteRef(masterPid).map(MasterRef) pipeTo self
        case Failure(e) =>
          log.error(e, "Failed to retrieve master PID.")
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
      val timeout: Timeout = context.system.settings.config.getDuration("akka.libprocess.timeout", TimeUnit.MILLISECONDS).millis

      if (schedulerRef.isEmpty)
        schedulerRef = Some(context.actorOf(Props(classOf[SchedulerActor], framework, self, masterRef, timeout, schedulerCreator)))

      schedulerRef.foreach(_ forward msg)
      context.become(ready)
      unstashAll()

    case msg @ FrameworkReregisteredMessage(id, _) =>
      scheduledTask.foreach(_.cancel())
      frameworkId = Some(id)
      schedulerRef.foreach { ref =>
        ref forward msg
        ref ! MasterRef(masterRef)
      }
      context.become(ready)
      unstashAll()

    case Terminated(ref) if ref == masterRef =>
      log.warning(s"Lost connection to master, trying to reconnect in ${connectionRetryDelay.toMillis}ms.")
      context.unwatch(ref)
      schedulerRef.foreach(_ ! MasterDisconnected)
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
  case object Connect
  case object MasterDisconnected
  case object Register
  case object Deactivate
}