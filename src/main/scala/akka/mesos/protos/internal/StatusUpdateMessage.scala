package akka.mesos.protos.internal

import mesos.internal.Messages

class StatusUpdateMessage(
    statusUpdate: StatusUpdate,
    pid: String) {
  def toProto: Messages.StatusUpdateMessage =
    Messages.StatusUpdateMessage
      .newBuilder
      .setUpdate(statusUpdate.toProto)
      .setPid(pid)
      .build()
}
