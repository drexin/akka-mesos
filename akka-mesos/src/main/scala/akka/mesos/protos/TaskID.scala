package akka.mesos.protos

import org.apache.mesos.Protos

final case class TaskID(value: String) extends ProtoWrapper[Protos.TaskID] {
  def toProto: Protos.TaskID =
    Protos.TaskID
      .newBuilder
      .setValue(value)
      .build()
}

object TaskID {
  def apply(proto: Protos.TaskID): TaskID = TaskID(proto.getValue)
}
