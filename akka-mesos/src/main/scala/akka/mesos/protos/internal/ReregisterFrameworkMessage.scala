package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, FrameworkInfo }
import mesos.internal.Messages

final case class ReregisterFrameworkMessage(
    frameworkInfo: FrameworkInfo,
    failover: Boolean) extends ProtoWrapper[Messages.ReregisterFrameworkMessage] {
  def toProto: Messages.ReregisterFrameworkMessage =
    Messages.ReregisterFrameworkMessage
      .newBuilder
      .setFramework(frameworkInfo.toProto)
      .setFailover(failover)
      .build()
}

object ReregisterFrameworkMessage {
  def fromBytes(bytes: Array[Byte]): ReregisterFrameworkMessage =
    ReregisterFrameworkMessage(Messages.ReregisterFrameworkMessage.parseFrom(bytes))

  def apply(proto: Messages.ReregisterFrameworkMessage): ReregisterFrameworkMessage =
    ReregisterFrameworkMessage(
      FrameworkInfo(proto.getFramework),
      proto.getFailover)
}
