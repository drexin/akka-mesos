package akka.mesos.protos

import org.apache.mesos.Protos

import scala.util.Try

final case class Parameter(key: String, value: String) extends ProtoWrapper[Protos.Parameter] {
  def toProto: Protos.Parameter =
    Protos.Parameter
      .newBuilder
      .setKey(key)
      .setValue(value)
      .build()
}

object Parameter extends ProtoReads[Parameter] {
  def apply(proto: Protos.Parameter): Parameter = Parameter(proto.getKey, proto.getValue)

  override def fromBytes(bytes: Array[Byte]): Try[Parameter] = Try {
    Parameter(Protos.Parameter.parseFrom(bytes))
  }
}
