package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, SlaveID, ExecutorID, FrameworkID }
import mesos.internal.Messages

final case class ExitedExecutorMessage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    executorId: ExecutorID,
    status: Int) extends ProtoWrapper[Messages.ExitedExecutorMessage] {
  def toProto: Messages.ExitedExecutorMessage =
    Messages.ExitedExecutorMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setExecutorId(executorId.toProto)
      .setStatus(status)
      .build()
}

object ExitedExecutorMessage {
  def fromBytes(bytes: Array[Byte]): ExitedExecutorMessage =
    ExitedExecutorMessage(Messages.ExitedExecutorMessage.parseFrom(bytes))

  def apply(proto: Messages.ExitedExecutorMessage): ExitedExecutorMessage =
    ExitedExecutorMessage(
      SlaveID(proto.getSlaveId),
      FrameworkID(proto.getFrameworkId),
      ExecutorID(proto.getExecutorId),
      proto.getStatus)
}
