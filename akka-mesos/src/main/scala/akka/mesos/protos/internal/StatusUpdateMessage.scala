package akka.mesos.protos.internal

import akka.libprocess.PID
import akka.mesos.protos.{ ProtoReads, ProtoWrapper }
import mesos.internal.Messages

import scala.util.Try

final case class StatusUpdateMessage(
    statusUpdate: StatusUpdate,
    pid: Option[PID]) extends ProtoWrapper[Messages.StatusUpdateMessage] with SchedulerMessage {
  def toProto: Messages.StatusUpdateMessage = {
    val builder = Messages.StatusUpdateMessage
      .newBuilder
      .setUpdate(statusUpdate.toProto)

    pid.foreach(x => builder.setPid(x.toAddressString))

    builder.build()
  }
}

object StatusUpdateMessage extends ProtoReads[StatusUpdateMessage] {
  def fromBytes(bytes: Array[Byte]): Try[StatusUpdateMessage] = Try {
    StatusUpdateMessage(Messages.StatusUpdateMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.StatusUpdateMessage): StatusUpdateMessage =
    StatusUpdateMessage(
      StatusUpdate(proto.getUpdate),
      if (proto.hasPid) PID.fromAddressString(proto.getPid).toOption else None)
}
