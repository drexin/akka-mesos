package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, FrameworkID, MasterInfo }
import mesos.internal.Messages

import scala.util.Try

final case class FrameworkReregisteredMessage(
    frameworkId: FrameworkID,
    masterInfo: MasterInfo) extends ProtoWrapper[Messages.FrameworkReregisteredMessage] {
  def toProto: Messages.FrameworkReregisteredMessage =
    Messages.FrameworkReregisteredMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setMasterInfo(masterInfo.toProto)
      .build()
}

object FrameworkReregisteredMessage extends ProtoReads[FrameworkReregisteredMessage] {
  def fromBytes(bytes: Array[Byte]): Try[FrameworkReregisteredMessage] = Try {
    FrameworkReregisteredMessage(Messages.FrameworkReregisteredMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.FrameworkReregisteredMessage): FrameworkReregisteredMessage =
    FrameworkReregisteredMessage(
      FrameworkID(proto.getFrameworkId),
      MasterInfo(proto.getMasterInfo))
}
