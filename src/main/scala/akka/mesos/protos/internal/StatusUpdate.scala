package akka.mesos.protos.internal

import akka.mesos.protos.{SlaveID, ExecutorID, TaskStatus, FrameworkID}
import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

final case class StatusUpdate(
    frameworkId: FrameworkID,
    status: TaskStatus,
    timestamp: Double,
    uuid: ByteString,
    executorId: Option[ExecutorID] = None,
    slaveId: Option[SlaveID] = None) {
  def toProto: Messages.StatusUpdate = {
    val builder = Messages.StatusUpdate
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setStatus(status.toProto)
      .setTimestamp(timestamp)
      .setUuid(PBByteString.copyFrom(uuid.toArray))

    executorId.foreach(x => builder.setExecutorId(x.toProto))
    slaveId.foreach(x => builder.setSlaveId(x.toProto))

    builder.build()
  }
}
