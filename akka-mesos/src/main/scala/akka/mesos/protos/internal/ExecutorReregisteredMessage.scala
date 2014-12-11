package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, SlaveInfo, SlaveID }
import mesos.internal.Messages

import scala.util.Try

final case class ExecutorReregisteredMessage(
    slaveId: SlaveID,
    slaveInfo: SlaveInfo) extends ProtoWrapper[Messages.ExecutorReregisteredMessage] {
  def toProto: Messages.ExecutorReregisteredMessage =
    Messages.ExecutorReregisteredMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setSlaveInfo(slaveInfo.toProto)
      .build()
}

object ExecutorReregisteredMessage extends ProtoReads[ExecutorReregisteredMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[ExecutorReregisteredMessage] = Try {
    ExecutorReregisteredMessage(Messages.ExecutorReregisteredMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.ExecutorReregisteredMessage): ExecutorReregisteredMessage =
    ExecutorReregisteredMessage(
      SlaveID(proto.getSlaveId),
      SlaveInfo(proto.getSlaveInfo)
    )
}
