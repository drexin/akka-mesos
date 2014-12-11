package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, FrameworkID }
import mesos.internal.Messages

import scala.util.Try

final case class UnregisterFrameworkMessage(frameworkId: FrameworkID) extends ProtoWrapper[Messages.UnregisterFrameworkMessage] {
  def toProto: Messages.UnregisterFrameworkMessage =
    Messages.UnregisterFrameworkMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .build()
}

object UnregisterFrameworkMessage extends ProtoReads[UnregisterFrameworkMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[UnregisterFrameworkMessage] = Try {
    UnregisterFrameworkMessage(Messages.UnregisterFrameworkMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.UnregisterFrameworkMessage): UnregisterFrameworkMessage =
    UnregisterFrameworkMessage(FrameworkID(proto.getFrameworkId))
}
