package akka.mesos.protos.internal

import mesos.internal.Messages

final case class FrameworkErrorMessage(message: String) {
  def toProto: Messages.FrameworkErrorMessage =
    Messages.FrameworkErrorMessage
      .newBuilder
      .setMessage(message)
      .build()
}
