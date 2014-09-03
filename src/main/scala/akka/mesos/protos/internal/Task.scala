package akka.mesos.protos.internal

import akka.mesos.protos._
import mesos.internal.Messages

import scala.collection.JavaConverters._

final case class Task(
    taskId: TaskID,
    frameworkId: FrameworkID,
    slaveId: SlaveID,
    state: TaskState,
    executorId: Option[ExecutorID] = None,
    resources: Seq[Resource] = None,
    statuses: Seq[TaskStatus] = Nil) {
  def toProto: Messages.Task = {
    val builder = Messages.Task
      .newBuilder
      .setTaskId(taskId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .setSlaveId(slaveId.toProto)
      .setState(state.toProto)
      .addAllResources(resources.map(_.toProto).asJava)
      .addAllStatuses(statuses.map(_.toProto).asJava)

    executorId.foreach(x => builder.setExecutorId(x.toProto))

    builder.build()
  }
}
