package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkID
import mesos.internal.Messages

final case class ReviveOffersMessage(frameworkId: FrameworkID) {
  def toProto: Messages.ReviveOffersMessage =
    Messages.ReviveOffersMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .build()
}
