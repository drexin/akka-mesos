package akka.mesos.protos.internal

import akka.mesos.protos._
import mesos.internal.Messages

import scala.util.Try

final case class ExecutorRegisteredMessage(
    executorInfo: ExecutorInfo,
    frameworkId: FrameworkID,
    frameworkInfo: FrameworkInfo,
    slaveId: SlaveID,
    slaveInfo: SlaveInfo) extends ProtoWrapper[Messages.ExecutorRegisteredMessage] {
  def toProto: Messages.ExecutorRegisteredMessage =
    Messages.ExecutorRegisteredMessage
      .newBuilder
      .setExecutorInfo(executorInfo.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setFrameworkInfo(frameworkInfo.toProto)
      .setSlaveId(slaveId.toProto)
      .setSlaveInfo(slaveInfo.toProto)
      .build()
}

object ExecutorRegisteredMessage extends ProtoReads[ExecutorRegisteredMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[ExecutorRegisteredMessage] = Try {
    ExecutorRegisteredMessage(Messages.ExecutorRegisteredMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.ExecutorRegisteredMessage): ExecutorRegisteredMessage =
    ExecutorRegisteredMessage(
      ExecutorInfo(proto.getExecutorInfo),
      FrameworkID(proto.getFrameworkId),
      FrameworkInfo(proto.getFrameworkInfo),
      SlaveID(proto.getSlaveId),
      SlaveInfo(proto.getSlaveInfo)
    )
}
