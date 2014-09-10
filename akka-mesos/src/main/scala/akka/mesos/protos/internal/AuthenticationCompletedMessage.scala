package akka.mesos.protos.internal

import mesos.internal.Messages

case object AuthenticationCompletedMessage {
  def toProto: Messages.AuthenticationCompletedMessage =
    Messages.AuthenticationCompletedMessage
      .newBuilder
      .build()
}
