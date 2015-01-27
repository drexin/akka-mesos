package akka.mesos.scheduler

import akka.actor.{ ActorRef, PoisonPill }
import akka.mesos.MesosFrameworkActor.Deactivate
import akka.mesos.protos._
import akka.mesos.stream.SchedulerDriverActor._
import akka.pattern.ask
import akka.util.{ ByteString, Timeout }

import scala.collection.immutable.Seq
import scala.concurrent.{ ExecutionContext, Future }

final class SchedulerDriver private[mesos] (
    frameworkInfo: FrameworkInfo,
    framework: ActorRef,
    driverRef: ActorRef)(implicit ec: ExecutionContext, timeout: Timeout) {

  def stop(failover: Boolean): Unit = {
    val res = if (!failover) {
      driverRef ? UnregisterFramework
    } else Future.successful(())

    res onComplete {
      case _ => framework ! PoisonPill
    }
  }

  def stop(): Unit = stop(failover = false)

  def abort(): Unit = {
    send(DeactivateFramework)
    framework ! Deactivate
  }

  def requestResources(requests: Seq[Request]): Unit =
    send(ResourceRequest(requests))

  def launchTasks(tasks: Seq[TaskInfo], offers: Seq[OfferID], filters: Filters): Unit =
    send(LaunchTasks(tasks, offers, filters))

  def launchTasks(tasks: Seq[TaskInfo], offers: Seq[OfferID]): Unit = launchTasks(tasks, offers, Filters(None))

  def killTask(taskId: TaskID): Unit = send(KillTask(taskId))

  def declineOffer(offerId: OfferID, filters: Filters): Unit =
    send(DeclineResourceOffer(offerId, filters))

  def declineOffer(offerId: OfferID): Unit = declineOffer(offerId, Filters(None))

  def reviveOffers(): Unit = send(ReviveOffers)

  def sendFrameworkMessage(executorId: ExecutorID, slaveId: SlaveID, data: ByteString): Unit =
    send(FrameworkMessage(slaveId, executorId, data))

  def reconcileTasks(statuses: Seq[TaskStatus]): Unit =
    send(ReconcileTasks(statuses))

  private def send(msg: SchedulerDriverMessage) = driverRef ! msg
}
