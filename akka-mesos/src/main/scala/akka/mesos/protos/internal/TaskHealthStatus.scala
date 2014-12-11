package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, TaskID }
import mesos.internal.Messages

import scala.util.Try

final case class TaskHealthStatus(
    taskId: TaskID,
    healthy: Boolean,
    killTask: Option[Boolean] = None,
    consecutiveFailures: Option[Int] = None) extends ProtoWrapper[Messages.TaskHealthStatus] {
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

object TaskHealthStatus extends ProtoReads[TaskHealthStatus] {
  override def fromBytes(bytes: Array[Byte]): Try[TaskHealthStatus] = Try {
    TaskHealthStatus(Messages.TaskHealthStatus.parseFrom(bytes))
  }

  def apply(proto: Messages.TaskHealthStatus): TaskHealthStatus =
    TaskHealthStatus(
      TaskID(proto.getTaskId),
      proto.getHealthy,
      if (proto.hasKillTask) Some(proto.getKillTask) else None,
      if (proto.hasConsecutiveFailures) Some(proto.getConsecutiveFailures) else None
    )
}
