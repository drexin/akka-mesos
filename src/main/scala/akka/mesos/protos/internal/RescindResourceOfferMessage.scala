package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, OfferID }
import mesos.internal.Messages

final case class RescindResourceOfferMessage(offerId: OfferID) extends ProtoWrapper[Messages.RescindResourceOfferMessage] {
  def toProto: Messages.RescindResourceOfferMessage =
    Messages.RescindResourceOfferMessage
      .newBuilder
      .setOfferId(offerId.toProto)
      .build()
}

object RescindResourceOfferMessage {
  def fromBytes(bytes: Array[Byte]): RescindResourceOfferMessage =
    RescindResourceOfferMessage(Messages.RescindResourceOfferMessage.parseFrom(bytes))

  def apply(proto: Messages.RescindResourceOfferMessage): RescindResourceOfferMessage =
    RescindResourceOfferMessage(OfferID(proto.getOfferId))
}
