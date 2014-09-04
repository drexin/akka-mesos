package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkID
import mesos.internal.Messages

final case class DeactivateFrameworkMessage(frameworkId: FrameworkID) {
  def toProto: Messages.DeactivateFrameworkMessage =
    Messages.DeactivateFrameworkMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .build()
}
