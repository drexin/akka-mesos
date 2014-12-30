package akka.mesos.protos

import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import org.apache.mesos.Protos
import org.scalacheck.Properties

import scala.util.Success

object TaskStatusSpecification extends Properties("TaskStatus") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (
    taskId: TaskID,
    state: TaskState,
    message: Option[String],
    data: Option[ByteString],
    slaveId: Option[SlaveID],
    executorId: Option[ExecutorID],
    timestamp: Option[Double],
    healthy: Option[Boolean]) =>
    val builder = Protos.TaskStatus
      .newBuilder
      .setTaskId(taskId.toProto)
      .setState(state.toProto)

    message.foreach(builder.setMessage)
    data.foreach(b => builder.setData(PBByteString.copyFrom(b.toArray)))
    slaveId.foreach(x => builder.setSlaveId(x.toProto))
    executorId.foreach(x => builder.setExecutorId(x.toProto))
    timestamp.foreach(builder.setTimestamp)
    healthy.foreach(builder.setHealthy)

    val proto = builder.build()

    TaskStatus(taskId, state, message, data, slaveId, executorId, timestamp, healthy).toProto == proto
  }

  property("apply") = forAll { (
    taskId: TaskID,
    state: TaskState,
    message: Option[String],
    data: Option[ByteString],
    slaveId: Option[SlaveID],
    executorId: Option[ExecutorID],
    timestamp: Option[Double],
    healthy: Option[Boolean]) =>
    val builder = Protos.TaskStatus
      .newBuilder
      .setTaskId(taskId.toProto)
      .setState(state.toProto)

    message.foreach(builder.setMessage)
    data.foreach(b => builder.setData(PBByteString.copyFrom(b.toArray)))
    slaveId.foreach(x => builder.setSlaveId(x.toProto))
    executorId.foreach(x => builder.setExecutorId(x.toProto))
    timestamp.foreach(builder.setTimestamp)
    healthy.foreach(builder.setHealthy)

    val proto = builder.build()

    TaskStatus(proto) == TaskStatus(taskId, state, message, data, slaveId, executorId, timestamp, healthy)
  }

  property("fromBytes") = forAll { (
    taskId: TaskID,
    state: TaskState,
    message: Option[String],
    data: Option[ByteString],
    slaveId: Option[SlaveID],
    executorId: Option[ExecutorID],
    timestamp: Option[Double],
    healthy: Option[Boolean]) =>
    val builder = Protos.TaskStatus
      .newBuilder
      .setTaskId(taskId.toProto)
      .setState(state.toProto)

    message.foreach(builder.setMessage)
    data.foreach(b => builder.setData(PBByteString.copyFrom(b.toArray)))
    slaveId.foreach(x => builder.setSlaveId(x.toProto))
    executorId.foreach(x => builder.setExecutorId(x.toProto))
    timestamp.foreach(builder.setTimestamp)
    healthy.foreach(builder.setHealthy)

    val proto = builder.build()

    TaskStatus.fromBytes(proto.toByteArray) == Success(TaskStatus(taskId, state, message, data, slaveId, executorId, timestamp, healthy))
  }
}
