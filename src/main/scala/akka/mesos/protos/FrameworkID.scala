package akka.mesos.protos

import org.apache.mesos.Protos
import org.apache.mesos.Protos.{ FrameworkID => PBFramworkID }

final case class FrameworkID(value: String) extends ProtoWrapper[Protos.FrameworkID] {
  def toProto: PBFramworkID =
    PBFramworkID.newBuilder
      .setValue(value)
      .build()
}

object FrameworkID {
  def fromBytes(bytes: Array[Byte]): FrameworkID =
    FrameworkID(Protos.FrameworkID.parseFrom(bytes))

  def apply(proto: Protos.FrameworkID): FrameworkID =
    FrameworkID(proto.getValue)
}
