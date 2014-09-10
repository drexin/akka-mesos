package akka.mesos.protos.internal

import akka.mesos.protos.{ TaskInfo, FrameworkID, ExecutorID }
import mesos.internal.Messages

import scala.collection.JavaConverters._

class ReregisterExecutorMessage(
    executorId: ExecutorID,
    frameworkId: FrameworkID,
    tasks: Seq[TaskInfo],
    updates: Seq[StatusUpdate]) {
  def toProto: Messages.ReregisterExecutorMessage =
    Messages.ReregisterExecutorMessage
      .newBuilder
      .setExecutorId(executorId.toProto)
      .setFrameworkId(frameworkId.toProto)
      .addAllTasks(tasks.map(_.toProto).asJava)
      .addAllUpdates(updates.map(_.toProto).asJava)
      .build()
}
