package akka.mesos.protos.internal

import akka.mesos.protos.SlaveID
import mesos.internal.Messages

final case class HeartbeatMessage(slaveId: SlaveID) {
  def toProto: Messages.HeartbeatMessage =
    Messages.HeartbeatMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}
