package akka.mesos.protos

import org.apache.mesos.Protos.{ OfferID => PBOfferID }

case class OfferID(value: String) {
  def toProtos: PBOfferID =
    PBOfferID
      .newBuilder
      .setValue(value)
      .build()
}
