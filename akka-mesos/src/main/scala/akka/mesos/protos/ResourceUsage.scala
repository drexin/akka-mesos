package akka.mesos.protos

import org.apache.mesos.Protos

import scala.util.Try

final case class ResourceUsage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    executorId: Option[ExecutorID],
    executorName: Option[String],
    taskId: Option[TaskID],
    statistics: Option[ResourceStatistics]) extends ProtoWrapper[Protos.ResourceUsage] {

  def toProto: Protos.ResourceUsage = {
    val builder = Protos.ResourceUsage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setFrameworkId(frameworkId.toProto)

    executorId.foreach(x => builder.setExecutorId(x.toProto))
    executorName.foreach(builder.setExecutorName)
    taskId.foreach(x => builder.setTaskId(x.toProto))
    statistics.foreach(x => builder.setStatistics(x.toProto))

    builder.build()
  }
}

object ResourceUsage extends ProtoReads[ResourceUsage] {
  override def fromBytes(bytes: Array[Byte]): Try[ResourceUsage] = Try {
    ResourceUsage(Protos.ResourceUsage.parseFrom(bytes))
  }

  def apply(proto: Protos.ResourceUsage): ResourceUsage = {
    ResourceUsage(
      SlaveID(proto.getSlaveId),
      FrameworkID(proto.getFrameworkId),
      if (proto.hasExecutorId) Some(ExecutorID(proto.getExecutorId)) else None,
      if (proto.hasExecutorName) Some(proto.getExecutorName) else None,
      if (proto.hasTaskId) Some(TaskID(proto.getTaskId)) else None,
      if (proto.hasStatistics) Some(ResourceStatistics(proto.getStatistics)) else None
    )
  }
}
