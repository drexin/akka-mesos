package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, OfferID }
import mesos.internal.Messages

import scala.util.Try

final case class RescindResourceOfferMessage(offerId: OfferID)
    extends ProtoWrapper[Messages.RescindResourceOfferMessage] {
  def toProto: Messages.RescindResourceOfferMessage =
    Messages.RescindResourceOfferMessage
      .newBuilder
      .setOfferId(offerId.toProto)
      .build()
}

object RescindResourceOfferMessage extends ProtoReads[RescindResourceOfferMessage] {
  def fromBytes(bytes: Array[Byte]): Try[RescindResourceOfferMessage] = Try {
    RescindResourceOfferMessage(Messages.RescindResourceOfferMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.RescindResourceOfferMessage): RescindResourceOfferMessage =
    RescindResourceOfferMessage(OfferID(proto.getOfferId))
}
