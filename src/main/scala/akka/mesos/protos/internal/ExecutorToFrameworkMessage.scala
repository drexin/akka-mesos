package akka.mesos.protos.internal

import akka.mesos.protos.{ FrameworkID, ExecutorID, SlaveID }
import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

final case class ExecutorToFrameworkMessage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    executorId: ExecutorID,
    data: ByteString) {
  def toProto: Messages.ExecutorToFrameworkMessage =
    Messages.ExecutorToFrameworkMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setExecutorId(executorId.toProto)
      .setData(PBByteString.copyFrom(data.toArray))
      .build()
}
