package akka.mesos.protos.internal

import akka.mesos.protos.ProtoWrapper
import mesos.internal.Messages

final case class FrameworkErrorMessage(message: String) extends ProtoWrapper[Messages.FrameworkErrorMessage] {
  def toProto: Messages.FrameworkErrorMessage =
    Messages.FrameworkErrorMessage
      .newBuilder
      .setMessage(message)
      .build()
}

object FrameworkErrorMessage {
  def fromBytes(bytes: Array[Byte]): FrameworkErrorMessage =
    FrameworkErrorMessage(Messages.FrameworkErrorMessage.parseFrom(bytes))

  def apply(proto: Messages.FrameworkErrorMessage): FrameworkErrorMessage =
    FrameworkErrorMessage(proto.getMessage)
}
