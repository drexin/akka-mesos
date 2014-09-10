package akka.mesos.protos

import akka.util.ByteString
import org.apache.mesos.Protos
import com.google.protobuf.{ ByteString => PBByteString }

final case class TaskStatus(
    taskId: TaskID,
    state: TaskState,
    message: Option[String] = None,
    data: Option[ByteString] = None,
    slaveId: Option[SlaveID] = None,
    executorId: Option[ExecutorID] = None,
    timestamp: Option[Double] = None,
    healthy: Option[Boolean] = None) extends ProtoWrapper[Protos.TaskStatus] {
  def toProto: Protos.TaskStatus = {
    val builder =
      Protos.TaskStatus
        .newBuilder
        .setTaskId(taskId.toProto)
        .setState(state.toProto)

    message.foreach(builder.setMessage)
    data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.toArray)))
    slaveId.foreach(id => builder.setSlaveId(id.toProto))
    executorId.foreach(id => builder.setExecutorId(id.toProto))
    timestamp.foreach(builder.setTimestamp)
    healthy.foreach(builder.setHealthy)

    builder.build()
  }
}

object TaskStatus {
  def apply(proto: Protos.TaskStatus): TaskStatus = {
    val message = if (proto.hasMessage) Some(proto.getMessage) else None
    val data = if (proto.hasData) Some(ByteString(proto.getData.toByteArray)) else None
    val slaveId = if (proto.hasSlaveId) Some(SlaveID(proto.getSlaveId)) else None
    val executorId = if (proto.hasExecutorId) Some(ExecutorID(proto.getExecutorId)) else None
    val timestamp = if (proto.hasTimestamp) Some(proto.getTimestamp) else None
    val healthy = if (proto.hasHealthy) Some(proto.getHealthy) else None

    TaskStatus(
      TaskID(proto.getTaskId),
      TaskState(proto.getState),
      message,
      data,
      slaveId,
      executorId,
      timestamp,
      healthy)
  }
}
