package akka.mesos.protos.internal

import akka.mesos.protos.SlaveID
import mesos.internal.Messages

class SlaveReregisteredMessage(slaveId: SlaveID) {
  def toProto: Messages.SlaveReregisteredMessage =
    Messages.SlaveReregisteredMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}
