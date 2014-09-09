package akka.mesos.protos.internal

import akka.mesos.protos._
import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages
import org.apache.mesos.Protos

final case class StatusUpdate(
    frameworkId: FrameworkID,
    status: TaskStatus,
    timestamp: Double,
    uuid: ByteString,
    executorId: Option[ExecutorID] = None,
    slaveId: Option[SlaveID] = None) extends ProtoWrapper[Messages.StatusUpdate] {
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

object StatusUpdate {
  def apply(proto: Messages.StatusUpdate): StatusUpdate = {
    val executorId = if (proto.hasExecutorId) Some(ExecutorID(proto.getExecutorId)) else None
    val slaveId = if (proto.hasSlaveId) Some(SlaveID(proto.getSlaveId)) else None

    StatusUpdate(
      FrameworkID(proto.getFrameworkId),
      TaskStatus(proto.getStatus),
      proto.getTimestamp,
      ByteString(proto.getUuid.toByteArray),
      executorId,
      slaveId)
  }
}
