package akka.mesos.protos

import org.apache.mesos.Protos

/*
message ResourceUsage {
  required SlaveID slave_id = 1;
  required FrameworkID framework_id = 2;

  // Resource usage is for an executor. For tasks launched with
  // an explicit executor, the executor id is provided. For tasks
  // launched without an executor, our internal executor will be
  // used. In this case, we provide the task id here instead, in
  // order to make this message easier for schedulers to work with.

  optional ExecutorID executor_id = 3; // If present, this executor was
  optional string executor_name = 4;   // explicitly specified.

  optional TaskID task_id = 5; // If present, the task did not have an executor.

  // If missing, the isolation module cannot provide resource usage.
  optional ResourceStatistics statistics = 6;
}
 */

case class ResourceUsage(
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
