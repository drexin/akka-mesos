package akka.mesos.protos.internal

import akka.mesos.protos.SlaveID
import mesos.internal.Messages

class UnregisterSlaveMessage(slaveId: SlaveID) {
  def toProto: Messages.UnregisterSlaveMessage =
    Messages.UnregisterSlaveMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}
