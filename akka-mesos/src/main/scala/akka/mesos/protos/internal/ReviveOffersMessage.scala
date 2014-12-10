package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, FrameworkID }
import mesos.internal.Messages

import scala.util.Try

final case class ReviveOffersMessage(frameworkId: FrameworkID) extends ProtoWrapper[Messages.ReviveOffersMessage] {
  def toProto: Messages.ReviveOffersMessage =
    Messages.ReviveOffersMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .build()
}

object ReviveOffersMessage extends ProtoReads[ReviveOffersMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[ReviveOffersMessage] = Try {
    ReviveOffersMessage(Messages.ReviveOffersMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.ReviveOffersMessage): ReviveOffersMessage =
    ReviveOffersMessage(FrameworkID(proto.getFrameworkId))
}
