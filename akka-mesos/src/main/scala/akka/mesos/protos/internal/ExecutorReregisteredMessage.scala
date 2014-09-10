package akka.mesos.protos.internal

import akka.mesos.protos.{ SlaveInfo, SlaveID }
import mesos.internal.Messages

class ExecutorReregisteredMessage(
    slaveId: SlaveID,
    slaveInfo: SlaveInfo) {
  def toProto: Messages.ExecutorReregisteredMessage =
    Messages.ExecutorReregisteredMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setSlaveInfo(slaveInfo.toProto)
      .build()
}
