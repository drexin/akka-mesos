package akka.mesos.protos.internal

import mesos.internal.Messages

final case class ShutdownMessage(message: Option[String] = None) {
  def toProto: Messages.ShutdownMessage = {
    val builder = Messages.ShutdownMessage.newBuilder

    message.foreach(builder.setMessage)

    builder.build()
  }
}
