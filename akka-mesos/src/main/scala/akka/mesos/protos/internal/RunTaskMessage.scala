package akka.mesos.protos.internal

import akka.mesos.protos.{ TaskInfo, FrameworkInfo, FrameworkID }
import mesos.internal.Messages

/*
required FrameworkID framework_id = 1;
  required FrameworkInfo framework = 2;
  required string pid = 3;
  required TaskInfo task = 4;
 */

final case class RunTaskMessage(
    frameworkId: FrameworkID,
    frameworkInfo: FrameworkInfo,
    pid: String,
    task: TaskInfo) {
  def toProto: Messages.RunTaskMessage =
    Messages.RunTaskMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setFramework(frameworkInfo.toProto)
      .setPid(pid)
      .setTask(task.toProto)
      .build()
}
