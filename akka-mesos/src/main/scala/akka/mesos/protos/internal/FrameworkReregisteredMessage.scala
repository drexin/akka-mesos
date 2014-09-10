package akka.mesos.protos.internal

import akka.mesos.protos.{ FrameworkID, MasterInfo }
import mesos.internal.Messages

final case class FrameworkReregisteredMessage(
    frameworkId: FrameworkID,
    masterInfo: MasterInfo) {
  def toProto: Messages.FrameworkReregisteredMessage =
    Messages.FrameworkReregisteredMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setMasterInfo(masterInfo.toProto)
      .build()
}
