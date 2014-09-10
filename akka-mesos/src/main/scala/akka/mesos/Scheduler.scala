package akka.mesos

import akka.actor.{ PoisonPill, ActorRef }
import akka.libprocess.LibProcessMessage
import akka.mesos.protos._
import akka.mesos.protos.internal._
import akka.pattern.ask
import akka.util.{ Timeout, ByteString }

import scala.collection.immutable.Seq
import scala.concurrent.{ ExecutionContext, Future }

abstract class Scheduler(protected val driver: SchedulerDriver) {
  def registered(frameworkId: FrameworkID, masterInfo: MasterInfo): Unit
  def reregistered(masterInfo: MasterInfo): Unit
  def resourceOffers(offers: Seq[Offer]): Unit
  def offerRescinded(offerId: OfferID): Unit
  def statusUpdate(status: TaskStatus): Unit
  def frameworkMessage(executorId: ExecutorID, slaveId: SlaveID, data: ByteString): Unit
  def disconnected(): Unit
  def slaveLost(slaveId: SlaveID): Unit
  def executorLost(executorId: ExecutorID, slaveId: SlaveID, status: Int): Unit
  def error(message: String): Unit
}

final class SchedulerDriver private[mesos] (
    frameworkInfo: FrameworkInfo,
    frameworkId: FrameworkID,
    framework: ActorRef,
    private[mesos] var master: ActorRef)(implicit ec: ExecutionContext, timeout: Timeout) {

  def stop(failover: Boolean): Unit = {
    val res = if (!failover) {
      master ? LibProcessMessage(frameworkInfo.name, UnregisterFrameworkMessage(frameworkId))
    } else Future.successful(())

    res onComplete {
      case _ => framework ! PoisonPill
    }
  }

  def stop(): Unit = stop(failover = false)

  def abort(): Unit = master ! LibProcessMessage(frameworkInfo.name, DeactivateFrameworkMessage)

  def requestResources(requests: Seq[Request]): Unit =
    send(ResourceRequestMessage(frameworkId, requests))

  def launchTasks(tasksToOfferIds: Seq[(TaskInfo, OfferID)], filters: Filters): Unit =
    send(LaunchTasksMessage(frameworkId, tasksToOfferIds, filters))

  def launchTasks(tasksToOfferIds: Seq[(TaskInfo, OfferID)]): Unit = launchTasks(tasksToOfferIds, Filters(None))

  def killTask(taskId: TaskID): Unit = send(KillTaskMessage(frameworkId, taskId))

  def declineOffer(offerId: OfferID, filters: Filters): Unit =
    send(DeclineResourceOfferMessage(frameworkId, offerId, filters))

  def declineOffer(offerId: OfferID): Unit = declineOffer(offerId, Filters(None))

  def reviveOffers(): Unit = send(ReviveOffersMessage(frameworkId))

  def sendFrameworkMessage(executorId: ExecutorID, slaveId: SlaveID, data: ByteString): Unit =
    send(FrameworkToExecutorMessage(slaveId, frameworkId, executorId, data))

  def reconcileTasks(statuses: Seq[TaskStatus]): Unit =
    send(ReconcileTasksMessage(frameworkId, statuses))

  private def send(msg: AnyRef) = master ! LibProcessMessage(frameworkInfo.name, msg)
}
