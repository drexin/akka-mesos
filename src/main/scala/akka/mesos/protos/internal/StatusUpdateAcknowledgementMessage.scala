package akka.mesos.protos.internal

import akka.mesos.protos.{TaskID, FrameworkID, SlaveID}
import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

final case class StatusUpdateAcknowledgementMessage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    taskId: TaskID,
    uuid: ByteString) {
  def toProto: Messages.StatusUpdateAcknowledgementMessage =
    Messages.StatusUpdateAcknowledgementMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setTaskId(taskId.toProto)
      .setUuid(PBByteString.copyFrom(uuid.toArray))
      .build()
}
