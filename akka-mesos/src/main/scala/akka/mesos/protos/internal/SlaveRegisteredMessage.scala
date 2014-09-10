package akka.mesos.protos.internal

import akka.mesos.protos.SlaveID
import mesos.internal.Messages

final case class SlaveRegisteredMessage(slaveId: SlaveID) {
  def toProto: Messages.SlaveRegisteredMessage =
    Messages.SlaveRegisteredMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}
