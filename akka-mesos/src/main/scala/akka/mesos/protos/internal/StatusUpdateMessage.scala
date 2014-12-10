package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper }
import mesos.internal.Messages

import scala.util.Try

final case class StatusUpdateMessage(
    statusUpdate: StatusUpdate,
    pid: String) extends ProtoWrapper[Messages.StatusUpdateMessage] {
  def toProto: Messages.StatusUpdateMessage =
    Messages.StatusUpdateMessage
      .newBuilder
      .setUpdate(statusUpdate.toProto)
      .setPid(pid)
      .build()
}

object StatusUpdateMessage extends ProtoReads[StatusUpdateMessage] {
  def fromBytes(bytes: Array[Byte]): Try[StatusUpdateMessage] = Try {
    StatusUpdateMessage(Messages.StatusUpdateMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.StatusUpdateMessage): StatusUpdateMessage =
    StatusUpdateMessage(
      StatusUpdate(proto.getUpdate),
      proto.getPid)
}
