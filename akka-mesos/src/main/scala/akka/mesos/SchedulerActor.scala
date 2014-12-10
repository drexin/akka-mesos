package akka.mesos

import akka.actor.{ Stash, ActorLogging, Actor, ActorRef }
import akka.libprocess.LibProcessMessage
import akka.mesos.MesosFrameworkActor.{ MasterRef, MasterDisconnected }
import akka.mesos.protos.FrameworkInfo
import akka.mesos.protos.internal._
import akka.util.Timeout

private[mesos] class SchedulerActor(
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
