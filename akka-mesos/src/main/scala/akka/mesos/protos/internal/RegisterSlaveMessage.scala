package akka.mesos.protos.internal

import akka.mesos.protos.SlaveInfo
import mesos.internal.Messages

class RegisterSlaveMessage(slaveInfo: SlaveInfo) {
  def toProto: Messages.RegisterSlaveMessage =
    Messages.RegisterSlaveMessage
      .newBuilder
      .setSlave(slaveInfo.toProto)
      .build()
}
