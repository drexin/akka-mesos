package akka.mesos.protos

import org.apache.mesos.Protos

final case class ResourceUsage(
    slaveId: SlaveID,
    frameworkId: FrameworkID,
    executorId: Option[ExecutorID],
    executorName: Option[String],
    taskId: Option[TaskID],
    statistics: Option[ResourceStatistics]) {

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
