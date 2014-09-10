package akka.mesos.protos.internal

import mesos.internal.Messages

case object AuthenticationFailedMessage {
  def toProto: Messages.AuthenticationFailedMessage =
    Messages.AuthenticationFailedMessage
      .newBuilder
      .build()
}
