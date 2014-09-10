package akka.mesos.protos.internal

import akka.mesos.protos.SlaveID
import mesos.internal.Messages

final case class ReconnectExecutorMessage(slaveId: SlaveID) {
  def toProto: Messages.ReconnectExecutorMessage =
    Messages.ReconnectExecutorMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .build()
}
