package akka.mesos.protos

import org.apache.mesos.Protos

final case class OfferID(value: String) extends ProtoWrapper[Protos.OfferID] {
  def toProto: Protos.OfferID =
    Protos.OfferID
      .newBuilder
      .setValue(value)
      .build()
}

object OfferID {
  def apply(proto: Protos.OfferID): OfferID = OfferID(proto.getValue)
}
