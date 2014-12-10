package akka.mesos.protos.internal

import akka.mesos.protos._
import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

import scala.util.Try

final case class FrameworkToExecutorMessage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    executorId: ExecutorID,
    data: ByteString) extends ProtoWrapper[Messages.FrameworkToExecutorMessage] {
  def toProto: Messages.FrameworkToExecutorMessage =
    Messages.FrameworkToExecutorMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setExecutorId(executorId.toProto)
      .setData(PBByteString.copyFrom(data.toArray))
      .build()
}

object FrameworkToExecutorMessage extends ProtoReads[FrameworkToExecutorMessage] {
  def fromBytes(bytes: Array[Byte]): Try[FrameworkToExecutorMessage] = Try {
    FrameworkToExecutorMessage(Messages.FrameworkToExecutorMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.FrameworkToExecutorMessage): FrameworkToExecutorMessage =
    FrameworkToExecutorMessage(
      SlaveID(proto.getSlaveId),
      FrameworkID(proto.getFrameworkId),
      ExecutorID(proto.getExecutorId),
      ByteString(proto.getData.toByteArray)
    )
}
