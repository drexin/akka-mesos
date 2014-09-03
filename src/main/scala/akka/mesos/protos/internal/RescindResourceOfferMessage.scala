package akka.mesos.protos.internal

import akka.mesos.protos.OfferID
import mesos.internal.Messages

final case class RescindResourceOfferMessage(offerId: OfferID) {
  def toProto: Messages.RescindResourceOfferMessage =
    Messages.RescindResourceOfferMessage
      .newBuilder
      .setOfferId(offerId.toProto)
      .build()
}
