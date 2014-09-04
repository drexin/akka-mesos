package akka.mesos.protos.internal

import akka.mesos.protos._
import mesos.internal.Messages

final case class ExecutorRegisteredMessage(
    executorInfo: ExecutorInfo,
    frameworkId: FrameworkID,
    frameworkInfo: FrameworkInfo,
    slaveId: SlaveID,
    slaveInfo: SlaveInfo) {
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
