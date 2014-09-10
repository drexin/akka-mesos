package akka.mesos.protos.internal

import akka.mesos.protos.TaskID
import mesos.internal.Messages

final case class TaskHealthStatus(
    taskId: TaskID,
    healthy: Boolean,
    killTask: Option[Boolean] = None,
    consecutiveFailures: Option[Int] = None) {
  def toProto: Messages.TaskHealthStatus = {
    val builder = Messages.TaskHealthStatus
      .newBuilder
      .setTaskId(taskId.toProto)
      .setHealthy(healthy)

    killTask.foreach(builder.setKillTask)
    consecutiveFailures.foreach(builder.setConsecutiveFailures)

    builder.build()
  }
}
