package akka.mesos.protos.internal

import akka.mesos.protos.{ExecutorID, FrameworkID, SlaveID}
import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

final case class FrameworkToExecutorMessage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    executorId: ExecutorID,
    data: ByteString) {
  def toProto: Messages.FrameworkToExecutorMessage =
    Messages.FrameworkToExecutorMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setExecutorId(executorId.toProto)
      .setData(PBByteString.copyFrom(data.toArray))
      .build()

}
