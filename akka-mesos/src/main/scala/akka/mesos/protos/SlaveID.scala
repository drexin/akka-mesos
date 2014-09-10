package akka.mesos.protos

import org.apache.mesos.Protos

final case class SlaveID(value: String) extends ProtoWrapper[Protos.SlaveID] {
  def toProto: Protos.SlaveID =
    Protos.SlaveID
      .newBuilder
      .setValue(value)
      .build()
}

object SlaveID {
  def apply(proto: Protos.SlaveID): SlaveID = SlaveID(proto.getValue)
}
