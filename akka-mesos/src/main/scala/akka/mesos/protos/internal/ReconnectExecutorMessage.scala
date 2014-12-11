package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, SlaveID }
import mesos.internal.Messages

import scala.util.Try

final case class ReconnectExecutorMessage(slaveId: SlaveID) extends ProtoWrapper[Messages.ReconnectExecutorMessage] {
  def toProto: Messages.ReconnectExecutorMessage =
    Messages.ReconnectExecutorMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}

object ReconnectExecutorMessage extends ProtoReads[ReconnectExecutorMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[ReconnectExecutorMessage] = Try {
    ReconnectExecutorMessage(Messages.ReconnectExecutorMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.ReconnectExecutorMessage): ReconnectExecutorMessage =
    ReconnectExecutorMessage(SlaveID(proto.getSlaveId))
}
