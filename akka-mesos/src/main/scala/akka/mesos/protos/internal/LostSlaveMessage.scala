package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, SlaveID }
import mesos.internal.Messages

final case class LostSlaveMessage(slaveId: SlaveID) extends ProtoWrapper[Messages.LostSlaveMessage] {
  def toProto: Messages.LostSlaveMessage =
    Messages.LostSlaveMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}

object LostSlaveMessage {
  def fromBytes(bytes: Array[Byte]): LostSlaveMessage =
    LostSlaveMessage(Messages.LostSlaveMessage.parseFrom(bytes))

  def apply(proto: Messages.LostSlaveMessage): LostSlaveMessage =
    LostSlaveMessage(SlaveID(proto.getSlaveId))
}
