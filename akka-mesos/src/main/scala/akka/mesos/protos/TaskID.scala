package akka.mesos.protos

import org.apache.mesos.Protos

import scala.util.Try

final case class TaskID(value: String) extends ProtoWrapper[Protos.TaskID] {
  def toProto: Protos.TaskID =
    Protos.TaskID
      .newBuilder
      .setValue(value)
      .build()
}

object TaskID extends ProtoReads[TaskID] {
  def apply(proto: Protos.TaskID): TaskID = TaskID(proto.getValue)

  override def fromBytes(bytes: Array[Byte]): Try[TaskID] = Try {
    TaskID(Protos.TaskID.parseFrom(bytes))
  }
}
