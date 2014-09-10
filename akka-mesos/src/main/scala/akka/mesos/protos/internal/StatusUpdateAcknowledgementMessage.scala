package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, TaskID, FrameworkID, SlaveID }
import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

final case class StatusUpdateAcknowledgementMessage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    taskId: TaskID,
    uuid: ByteString) extends ProtoWrapper[Messages.StatusUpdateAcknowledgementMessage] {
  def toProto: Messages.StatusUpdateAcknowledgementMessage =
    Messages.StatusUpdateAcknowledgementMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setTaskId(taskId.toProto)
      .setUuid(PBByteString.copyFrom(uuid.toArray))
      .build()
}

object StatusUpdateAcknowledgementMessage {
  def fromBytes(bytes: Array[Byte]): StatusUpdateAcknowledgementMessage =
    StatusUpdateAcknowledgementMessage(Messages.StatusUpdateAcknowledgementMessage.parseFrom(bytes))

  def apply(proto: Messages.StatusUpdateAcknowledgementMessage): StatusUpdateAcknowledgementMessage =
    StatusUpdateAcknowledgementMessage(
      SlaveID(proto.getSlaveId),
      FrameworkID(proto.getFrameworkId),
      TaskID(proto.getTaskId),
      ByteString(proto.getUuid.toByteArray))
}
