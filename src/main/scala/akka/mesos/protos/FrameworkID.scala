package akka.mesos.protos

import org.apache.mesos.Protos.{ FrameworkID => PBFramworkID }

case class FrameworkID(value: String) {
  def toProtos: PBFramworkID =
    PBFramworkID.newBuilder
      .setValue(value)
      .build()
}
