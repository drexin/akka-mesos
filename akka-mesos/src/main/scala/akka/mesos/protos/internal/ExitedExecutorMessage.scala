package akka.mesos.protos.internal

import akka.mesos.protos._
import mesos.internal.Messages

import scala.util.Try

final case class ExitedExecutorMessage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    executorId: ExecutorID,
    status: Int) extends ProtoWrapper[Messages.ExitedExecutorMessage] with SchedulerMessage {
  def toProto: Messages.ExitedExecutorMessage =
    Messages.ExitedExecutorMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setExecutorId(executorId.toProto)
      .setStatus(status)
      .build()
}

object ExitedExecutorMessage extends ProtoReads[ExitedExecutorMessage] {
  def fromBytes(bytes: Array[Byte]): Try[ExitedExecutorMessage] = Try {
    ExitedExecutorMessage(Messages.ExitedExecutorMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.ExitedExecutorMessage): ExitedExecutorMessage =
    ExitedExecutorMessage(
      SlaveID(proto.getSlaveId),
      FrameworkID(proto.getFrameworkId),
      ExecutorID(proto.getExecutorId),
      proto.getStatus)
}
