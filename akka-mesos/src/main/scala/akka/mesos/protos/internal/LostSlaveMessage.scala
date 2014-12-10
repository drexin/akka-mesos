package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, SlaveID }
import mesos.internal.Messages

import scala.util.Try

final case class LostSlaveMessage(slaveId: SlaveID) extends ProtoWrapper[Messages.LostSlaveMessage] {
  def toProto: Messages.LostSlaveMessage =
    Messages.LostSlaveMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}

object LostSlaveMessage extends ProtoReads[LostSlaveMessage] {
  def fromBytes(bytes: Array[Byte]): Try[LostSlaveMessage] = Try {
    LostSlaveMessage(Messages.LostSlaveMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.LostSlaveMessage): LostSlaveMessage =
    LostSlaveMessage(SlaveID(proto.getSlaveId))
}
