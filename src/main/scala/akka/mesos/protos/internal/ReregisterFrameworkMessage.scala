package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkInfo
import mesos.internal.Messages

final case class ReregisterFrameworkMessage(
    frameworkInfo: FrameworkInfo,
    failover: Boolean) {
  def toProto: Messages.ReregisterFrameworkMessage =
    Messages.ReregisterFrameworkMessage
      .newBuilder
      .setFramework(frameworkInfo.toProto)
      .setFailover(failover)
      .build()
}
