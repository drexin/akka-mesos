package akka.mesos.protos.internal

import akka.mesos.protos.ProtoWrapper
import mesos.internal.Messages

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

object StatusUpdateMessage {
  def fromBytes(bytes: Array[Byte]): StatusUpdateMessage =
    StatusUpdateMessage(Messages.StatusUpdateMessage.parseFrom(bytes))

  def apply(proto: Messages.StatusUpdateMessage): StatusUpdateMessage =
    StatusUpdateMessage(
      StatusUpdate(proto.getUpdate),
      proto.getPid)
}
