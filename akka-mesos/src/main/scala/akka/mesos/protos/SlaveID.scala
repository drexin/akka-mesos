package akka.mesos.protos

import org.apache.mesos.Protos

import scala.util.Try

final case class SlaveID(value: String) extends ProtoWrapper[Protos.SlaveID] {
  def toProto: Protos.SlaveID =
    Protos.SlaveID
      .newBuilder
      .setValue(value)
      .build()
}

object SlaveID extends ProtoReads[SlaveID] {
  def apply(proto: Protos.SlaveID): SlaveID = SlaveID(proto.getValue)

  override def fromBytes(bytes: Array[Byte]): Try[SlaveID] = Try {
    SlaveID(Protos.SlaveID.parseFrom(bytes))
  }
}
