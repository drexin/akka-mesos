package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, FrameworkID, MasterInfo }
import mesos.internal.Messages

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

object FrameworkReregisteredMessage {
  def fromBytes(bytes: Array[Byte]): FrameworkReregisteredMessage =
    FrameworkReregisteredMessage(Messages.FrameworkReregisteredMessage.parseFrom(bytes))

  def apply(proto: Messages.FrameworkReregisteredMessage): FrameworkReregisteredMessage =
    FrameworkReregisteredMessage(
      FrameworkID(proto.getFrameworkId),
      MasterInfo(proto.getMasterInfo))
}
