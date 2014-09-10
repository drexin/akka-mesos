package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkID
import mesos.internal.Messages

final case class UpdateFrameworkMessage(
    frameworkId: FrameworkID,
    pid: String) {
  def toProto: Messages.UpdateFrameworkMessage =
    Messages.UpdateFrameworkMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setPid(pid)
      .build()
}
