package akka.mesos.stream

import akka.actor.ActorRef
import akka.mesos.MesosFrameworkActor.RegisterScheduler
import akka.mesos.protos.internal.SchedulerMessage
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{ Cancel, Request }

import scala.annotation.tailrec

object SchedulerPublisher {
  case object MessageAccepted
  case object MessageDropped
}

private[mesos] class SchedulerPublisher(frameworkActor: ActorRef) extends ActorPublisher[SchedulerMessage] {
  import akka.mesos.stream.SchedulerPublisher._

  val MaxBufferSize = context.system.settings.config.getInt("akka.mesos.scheduler.stream.maxBufferSize")
  var buf = Vector.empty[SchedulerMessage]

  override def preStart(): Unit = {
    frameworkActor ! RegisterScheduler(self)
  }

  override def receive = {
    case msg: SchedulerMessage if buf.size == MaxBufferSize =>
      sender() ! MessageDropped
    case msg: SchedulerMessage =>
      sender() ! MessageAccepted
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
