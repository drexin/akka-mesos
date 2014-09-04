package akka.mesos.protos.internal

import mesos.internal.Messages

final case class AuthenticateMessage(pid: String) {
  def toProto: Messages.AuthenticateMessage =
    Messages.AuthenticateMessage
      .newBuilder
      .setPid(pid)
      .build()
}
