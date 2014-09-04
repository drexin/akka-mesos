package akka.mesos.protos.internal

import mesos.internal.Messages

final case class AuthenticationStartMessage(
    mechanism: String,
    data: Option[String] = None) {
  def toProto: Messages.AuthenticationStartMessage = {
    val builder = Messages.AuthenticationStartMessage
      .newBuilder()
      .setMechanism(mechanism)

    data.foreach(builder.setData)

    builder.build()
  }
}
