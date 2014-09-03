package akka.mesos.protos.internal

import akka.mesos.protos.SlaveID
import mesos.internal.Messages

final case class LostSlaveMessage(slaveId: SlaveID) {
  def toProto: Messages.LostSlaveMessage =
    Messages.LostSlaveMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}
