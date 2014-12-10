package akka.mesos.protos.internal

import akka.mesos.protos._
import mesos.internal.Messages

import scala.util.Try

final case class RunTaskMessage(
    frameworkId: FrameworkID,
    frameworkInfo: FrameworkInfo,
    pid: String,
    task: TaskInfo) extends ProtoWrapper[Messages.RunTaskMessage] {
  def toProto: Messages.RunTaskMessage =
    Messages.RunTaskMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .setFramework(frameworkInfo.toProto)
      .setPid(pid)
      .setTask(task.toProto)
      .build()
}

object RunTaskMessage extends ProtoReads[RunTaskMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[RunTaskMessage] = Try {
    RunTaskMessage(Messages.RunTaskMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.RunTaskMessage): RunTaskMessage =
    RunTaskMessage(
      FrameworkID(proto.getFrameworkId),
      FrameworkInfo(proto.getFramework),
      proto.getPid,
      TaskInfo(proto.getTask)
    )
}
