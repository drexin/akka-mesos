package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, FrameworkInfo }
import mesos.internal.Messages

final case class RegisterFrameworkMessage(frameworkInfo: FrameworkInfo) extends ProtoWrapper[Messages.RegisterFrameworkMessage] {
  def toProto: Messages.RegisterFrameworkMessage =
    Messages.RegisterFrameworkMessage
      .newBuilder
      .setFramework(frameworkInfo.toProto)
      .build()
}

object RegisterFrameworkMessage {
  def fromBytes(bytes: Array[Byte]): RegisterFrameworkMessage =
    RegisterFrameworkMessage(Messages.RegisterFrameworkMessage.parseFrom(bytes))

  def apply(proto: Messages.RegisterFrameworkMessage): RegisterFrameworkMessage =
    RegisterFrameworkMessage(FrameworkInfo(proto.getFramework))
}
