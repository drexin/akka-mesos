package akka.mesos.scheduler

import akka.actor.{ ActorRef, PoisonPill }
import akka.libprocess.LibProcessMessage
import akka.mesos.MesosFrameworkActor.Deactivate
import akka.mesos.protos._
import akka.mesos.protos.internal._
import akka.pattern.ask
import akka.util.{ ByteString, Timeout }

import scala.collection.immutable.Seq
import scala.concurrent.{ ExecutionContext, Future }

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

  def abort(): Unit = {
    send(DeactivateFrameworkMessage(frameworkId))
    framework ! Deactivate
  }

  def requestResources(requests: Seq[Request]): Unit =
    send(ResourceRequestMessage(frameworkId, requests))

  def launchTasks(tasks: Seq[TaskInfo], offers: Seq[OfferID], filters: Filters): Unit =
    send(LaunchTasksMessage(frameworkId, tasks, offers, filters))

  def launchTasks(tasks: Seq[TaskInfo], offers: Seq[OfferID]): Unit = launchTasks(tasks, offers, Filters(None))

  def killTask(taskId: TaskID): Unit = send(KillTaskMessage(frameworkId, taskId))

  def declineOffer(offerId: OfferID, filters: Filters): Unit =
    send(DeclineResourceOfferMessage(frameworkId, offerId, filters))

  def declineOffer(offerId: OfferID): Unit = declineOffer(offerId, Filters(None))

  def reviveOffers(): Unit = send(ReviveOffersMessage(frameworkId))

  def sendFrameworkMessage(executorId: ExecutorID, slaveId: SlaveID, data: ByteString): Unit =
    send(FrameworkToExecutorMessage(slaveId, frameworkId, executorId, data))

  def reconcileTasks(statuses: Seq[TaskStatus]): Unit =
    send(ReconcileTasksMessage(frameworkId, statuses))

  private def send(msg: ProtoWrapper[_]) = master ! LibProcessMessage(frameworkInfo.name, msg)
}
