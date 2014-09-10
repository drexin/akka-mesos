package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, FrameworkID, ExecutorID, SlaveID }
import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

final case class ExecutorToFrameworkMessage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    executorId: ExecutorID,
    data: ByteString) extends ProtoWrapper[Messages.ExecutorToFrameworkMessage] {
  def toProto: Messages.ExecutorToFrameworkMessage =
    Messages.ExecutorToFrameworkMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setExecutorId(executorId.toProto)
      .setData(PBByteString.copyFrom(data.toArray))
      .build()
}

object ExecutorToFrameworkMessage {
  def fromBytes(bytes: Array[Byte]): ExecutorToFrameworkMessage =
    ExecutorToFrameworkMessage(Messages.ExecutorToFrameworkMessage.parseFrom(bytes))

  def apply(proto: Messages.ExecutorToFrameworkMessage): ExecutorToFrameworkMessage =
    ExecutorToFrameworkMessage(
      SlaveID(proto.getSlaveId),
      FrameworkID(proto.getFrameworkId),
      ExecutorID(proto.getExecutorId),
      ByteString(proto.getData.toByteArray))
}
