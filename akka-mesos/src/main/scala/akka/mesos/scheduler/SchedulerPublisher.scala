package akka.mesos.scheduler

import akka.actor.ActorRef
import akka.mesos.MesosFrameworkActor.RegisterScheduler
import akka.mesos.protos._
import akka.mesos.protos.internal._
import akka.mesos.scheduler.SchedulerPublisher.SchedulerMessage
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{ Cancel, Request }
import akka.util.ByteString

import scala.annotation.tailrec
import scala.collection.immutable.Queue

object SchedulerPublisher {
  object SchedulerMessage {
    def apply(msg: ProtoWrapper[_]): Option[SchedulerMessage] = msg match {
      case ExecutorToFrameworkMessage(slaveId, _, executorId, data) =>
        Some(FrameworkMessage(executorId, slaveId, data))
      case ExitedExecutorMessage(slaveId, _, executorId, status) =>
        Some(ExecutorLost(executorId, slaveId, status))
      case FrameworkErrorMessage(message) =>
        Some(Error(message))
      case FrameworkRegisteredMessage(frameworkId, masterInfo) =>
        Some(FrameworkRegistered(frameworkId, masterInfo))
      case FrameworkReregisteredMessage(frameworkId, masterInfo) =>
        Some(FrameworkReregistered(masterInfo))
      case LostSlaveMessage(slaveId) =>
        Some(SlaveLost(slaveId))
      case RescindResourceOfferMessage(offerId) =>
        Some(ResourceOfferRescinded(offerId))
      case ResourceOffersMessage(offers, _) =>
        Some(ResourceOffers(offers))
      case StatusUpdateMessage(statusUpdate, _) =>
        Some(StatusUpdate(statusUpdate))
      case _ => None
    }
  }

  sealed trait SchedulerMessage

  case object Disconnected extends SchedulerMessage
  final case class FrameworkMessage(executorId: ExecutorID, slaveId: SlaveID, data: ByteString) extends SchedulerMessage
  final case class ExecutorLost(executorId: ExecutorID, slaveId: SlaveID, status: Int) extends SchedulerMessage
  final case class Error(message: String) extends SchedulerMessage
  final case class FrameworkRegistered(frameworkId: FrameworkID, masterInfo: MasterInfo) extends SchedulerMessage
  final case class FrameworkReregistered(masterInfo: MasterInfo) extends SchedulerMessage
  final case class SlaveLost(slaveId: SlaveID) extends SchedulerMessage
  final case class ResourceOfferRescinded(offerId: OfferID) extends SchedulerMessage
  final case class ResourceOffers(offers: Seq[Offer]) extends SchedulerMessage
  final case class StatusUpdate(update: internal.StatusUpdate) extends SchedulerMessage
}

private[mesos] final class SchedulerPublisher(frameworkActor: ActorRef) extends ActorPublisher[SchedulerMessage] {

  var buf = Queue.empty[SchedulerMessage]

  override def preStart(): Unit = {
    frameworkActor ! RegisterScheduler(self)
  }

  override def receive = {
    case msg: SchedulerMessage =>
      if (buf.isEmpty && totalDemand > 0) {
        onNext(msg)
      } else {
        buf :+= msg
        deliverBuf()
      }
    case Request(_) =>
      deliverBuf()
    case Cancel =>
      context.stop(self)
  }

  @tailrec
  final def deliverBuf(): Unit =
    if (totalDemand > 0) {
      if (totalDemand <= Int.MaxValue) {
        val (use, keep) = buf.splitAt(totalDemand.toInt)
        buf = keep
        use foreach onNext
      } else {
        val (use, keep) = buf.splitAt(Int.MaxValue)
        buf = keep
        use foreach onNext
        deliverBuf()
      }
    }
}
