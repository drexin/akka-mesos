package akka.mesos.protos.internal

import akka.mesos.protos.{TaskID, FrameworkID}
import mesos.internal.Messages

final case class KillTaskMessage(
    frameworkId: FrameworkID,
    taskId: TaskID) {
  def toProto: Messages.KillTaskMessage =
    Messages.KillTaskMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setTaskId(taskId.toProto)
      .build()
}
