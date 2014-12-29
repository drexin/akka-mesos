package akka.mesos.protos

import org.apache.mesos.Protos

import scala.util.Try

final case class ContainerID(value: String) extends ProtoWrapper[Protos.ContainerID] {
  def toProto: Protos.ContainerID = Protos.ContainerID.newBuilder().setValue(value).build()
}

object ContainerID extends ProtoReads[ContainerID] {
  def fromBytes(bytes: Array[Byte]): Try[ContainerID] = Try {
    ContainerID(Protos.ContainerID.parseFrom(bytes))
  }

  def apply(proto: Protos.ContainerID): ContainerID = ContainerID(proto.getValue)
}
