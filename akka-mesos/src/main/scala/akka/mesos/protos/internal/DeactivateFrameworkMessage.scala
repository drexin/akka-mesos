package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, FrameworkID }
import mesos.internal.Messages

import scala.util.Try

final case class DeactivateFrameworkMessage(frameworkId: FrameworkID) extends ProtoWrapper[Messages.DeactivateFrameworkMessage] {
  def toProto: Messages.DeactivateFrameworkMessage =
    Messages.DeactivateFrameworkMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .build()
}

object DeactivateFrameworkMessage extends ProtoReads[DeactivateFrameworkMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[DeactivateFrameworkMessage] = Try {
    DeactivateFrameworkMessage(Messages.DeactivateFrameworkMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.DeactivateFrameworkMessage): DeactivateFrameworkMessage =
    DeactivateFrameworkMessage(FrameworkID(proto.getFrameworkId))
}
