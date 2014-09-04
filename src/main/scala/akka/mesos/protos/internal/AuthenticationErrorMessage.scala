package akka.mesos.protos.internal

import mesos.internal.Messages

final case class AuthenticationErrorMessage(error: String) {
  def toProto: Messages.AuthenticationErrorMessage =
    Messages.AuthenticationErrorMessage
      .newBuilder
      .setError(error)
      .build()
}
