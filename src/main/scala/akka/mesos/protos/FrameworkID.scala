package akka.mesos.protos

import org.apache.mesos.Protos.{ FrameworkID => PBFramworkID }

final case class FrameworkID(value: String) {
  def toProto: PBFramworkID =
    PBFramworkID.newBuilder
      .setValue(value)
      .build()
}
