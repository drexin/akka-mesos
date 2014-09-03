package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkInfo
import mesos.internal.Messages

final case class RegisterFrameworkMessage(frameworkInfo: FrameworkInfo) {
  def toProto: Messages.RegisterFrameworkMessage =
    Messages.RegisterFrameworkMessage
      .newBuilder
      .setFramework(frameworkInfo.toProto)
      .build()
}
