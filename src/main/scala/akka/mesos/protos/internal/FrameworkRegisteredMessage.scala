package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, FrameworkID, MasterInfo }
import mesos.internal.Messages

final case class FrameworkRegisteredMessage(
    frameworkId: FrameworkID,
    masterInfo: MasterInfo) extends ProtoWrapper[Messages.FrameworkRegisteredMessage] {
  def toProto: Messages.FrameworkRegisteredMessage =
    Messages.FrameworkRegisteredMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setMasterInfo(masterInfo.toProto)
      .build()
}

object FrameworkRegisteredMessage {
  /**
    * Creates a [[FrameworkRegisteredMessage]] from a Protobuf byte array
    */
  def fromBytes(bytes: Array[Byte]): FrameworkRegisteredMessage =
    FrameworkRegisteredMessage(Messages.FrameworkRegisteredMessage.parseFrom(bytes))

  /**
    * Creates a [[FrameworkRegisteredMessage]] from a Protobuf message
    */
  def apply(proto: Messages.FrameworkRegisteredMessage): FrameworkRegisteredMessage =
    FrameworkRegisteredMessage(
      FrameworkID(proto.getFrameworkId),
      MasterInfo(proto.getMasterInfo)
    )
}
