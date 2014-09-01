package akka.mesos.protos

import org.apache.mesos.Protos

final case class ContainerID(value: String) {
  def toProto: Protos.ContainerID = Protos.ContainerID.newBuilder().setValue(value).build()
}
