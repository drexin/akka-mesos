package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkID
import mesos.internal.Messages

final case class FrameworkExpiredMessage(frameworkId: FrameworkID) {
  def toProto: Messages.FrameworkExpiredMessage =
    Messages.FrameworkExpiredMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .build()
}
