package akka.mesos.scheduler

import akka.actor.{ Actor, ActorLogging, ActorRef, Stash }
import akka.libprocess.{ LibProcess, LibProcessMessage }
import akka.mesos.MesosFrameworkActor.{ MasterDisconnected, MasterRef }
import akka.mesos.protos.FrameworkInfo
import akka.mesos.protos.internal._
import akka.util.Timeout

private[mesos] class SchedulerActor(
    frameworkInfo: FrameworkInfo,
    framework: ActorRef,
    master: ActorRef,
    timeout: Timeout,
    schedulerCreator: SchedulerDriver => Scheduler) extends Actor with ActorLogging with Stash {
  import context.dispatcher

  var scheduler: Scheduler = _
  var driver: SchedulerDriver = _
  val libprocess = LibProcess(context.system)

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
    case StatusUpdateMessage(StatusUpdate(frameworkId, status, _, uuid, _, slaveIdOpt), pid) =>
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

    case FrameworkRegisteredMessage(frameworkId, masterInfo)   => scheduler.registered(frameworkId, masterInfo)
    case FrameworkReregisteredMessage(frameworkId, masterInfo) => scheduler.reregistered(masterInfo)
  }

  def disconnected: Receive = {
    case MasterRef(ref) =>
      driver.master = ref
      context.become(ready)
      unstashAll()

    case _ => stash()
  }
}
