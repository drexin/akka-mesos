package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper }
import mesos.internal.Messages

import scala.util.Try

final case class FrameworkErrorMessage(message: String)
    extends ProtoWrapper[Messages.FrameworkErrorMessage] {
  def toProto: Messages.FrameworkErrorMessage =
    Messages.FrameworkErrorMessage
      .newBuilder
      .setMessage(message)
      .build()
}

object FrameworkErrorMessage extends ProtoReads[FrameworkErrorMessage] {
  def fromBytes(bytes: Array[Byte]): Try[FrameworkErrorMessage] = Try {
    FrameworkErrorMessage(Messages.FrameworkErrorMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.FrameworkErrorMessage): FrameworkErrorMessage =
    FrameworkErrorMessage(proto.getMessage)
}
