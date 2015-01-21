package akka.mesos.protos

import akka.util.ByteString
import org.apache.mesos.Protos
import com.google.protobuf.{ ByteString => PBByteString }
import scala.collection.immutable.Seq
import scala.collection.JavaConverters._
import scala.util.Try

final case class TaskInfo(
    name: String,
    taskId: TaskID,
    slaveId: SlaveID,
    resources: Seq[Resource],
    executor: Option[ExecutorInfo] = None,
    command: Option[CommandInfo] = None,
    data: Option[ByteString] = None,
    healthCheck: Option[HealthCheck] = None) extends ProtoWrapper[Protos.TaskInfo] {
  def toProto: Protos.TaskInfo = {
    val builder =
      Protos.TaskInfo
        .newBuilder
        .setName(name)
        .setTaskId(taskId.toProto)
        .setSlaveId(slaveId.toProto)
        .addAllResources(resources.map(_.toProto).asJava)

    executor.foreach(e => builder.setExecutor(e.toProto))
    command.foreach(c => builder.setCommand(c.toProto))
    data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.toArray)))
    healthCheck.foreach(hs => builder.setHealthCheck(hs.toProto))

    builder.build()
  }
}

object TaskInfo extends ProtoReads[TaskInfo] {
  def apply(proto: Protos.TaskInfo): TaskInfo = {
    val executor = if (proto.hasExecutor) Some(ExecutorInfo(proto.getExecutor)) else None
    val command = if (proto.hasCommand) Some(CommandInfo(proto.getCommand)) else None
    val data = if (proto.hasData) Some(ByteString(proto.getData.toByteArray)) else None
    val healthCheck = if (proto.hasHealthCheck) Some(HealthCheck(proto.getHealthCheck)) else None

    TaskInfo(
      proto.getName,
      TaskID(proto.getTaskId),
      SlaveID(proto.getSlaveId),
      proto.getResourcesList.asScala.to[Seq].map(Resource(_)),
      executor,
      command,
      data,
      healthCheck)
  }

  override def fromBytes(bytes: Array[Byte]): Try[TaskInfo] = Try {
    TaskInfo(Protos.TaskInfo.parseFrom(bytes))
  }
}
