package akka.mesos.protos

import org.apache.mesos.Protos

case class ContainerID(value: String) {
  def toProto: Protos.ContainerID = Protos.ContainerID.newBuilder().setValue(value).build()
}
