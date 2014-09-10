package akka.mesos.protos

import org.apache.mesos.Protos

final case class ExecutorID(value: String) extends ProtoWrapper[Protos.ExecutorID] {
  def toProto: Protos.ExecutorID =
    Protos.ExecutorID
      .newBuilder
      .setValue(value)
      .build()
}

object ExecutorID {
  def apply(proto: Protos.ExecutorID): ExecutorID = ExecutorID(proto.getValue)
}
