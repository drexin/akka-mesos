package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkID
import mesos.internal.Messages

final case class ShutdownFrameworkMessage(frameworkId: FrameworkID) {
  def toProto: Messages.ShutdownFrameworkMessage =
    Messages.ShutdownFrameworkMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .build()
}
