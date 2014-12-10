package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, TaskID, FrameworkID }
import mesos.internal.Messages

import scala.util.Try

final case class KillTaskMessage(
    frameworkId: FrameworkID,
    taskId: TaskID) extends ProtoWrapper[Messages.KillTaskMessage] {
  def toProto: Messages.KillTaskMessage =
    Messages.KillTaskMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setTaskId(taskId.toProto)
      .build()
}

object KillTaskMessage extends ProtoReads[KillTaskMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[KillTaskMessage] = Try {
    KillTaskMessage(Messages.KillTaskMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.KillTaskMessage): KillTaskMessage =
    KillTaskMessage(
      FrameworkID(proto.getFrameworkId),
      TaskID(proto.getTaskId)
    )
}
