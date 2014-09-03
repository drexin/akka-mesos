package akka.mesos.protos.internal

import akka.mesos.protos.{FrameworkID, MasterInfo}
import mesos.internal.Messages

final case class FrameworkRegisteredMessage(
    frameworkId: FrameworkID,
    masterInfo: MasterInfo) {
  def toProto: Messages.FrameworkRegisteredMessage =
    Messages.FrameworkRegisteredMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setMasterInfo(masterInfo.toProto)
      .build()
}
