package akka.mesos.protos.internal

import akka.mesos.protos.{ ExecutorID, FrameworkID }
import mesos.internal.Messages

final case class RegisterExecutorMessage(
    frameworkId: FrameworkID,
    executorId: ExecutorID) {
  def toProto: Messages.RegisterExecutorMessage =
    Messages.RegisterExecutorMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setExecutorId(executorId.toProto)
      .build()
}
