package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkID
import mesos.internal.Messages

final case class UnregisterFrameworkMessage(frameworkId: FrameworkID) {
  def toProto: Messages.UnregisterFrameworkMessage =
    Messages.UnregisterFrameworkMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .build()
}
