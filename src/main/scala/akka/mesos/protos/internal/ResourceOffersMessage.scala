package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoWrapper, Offer }
import mesos.internal.Messages

import scala.collection.JavaConverters._

final case class ResourceOffersMessage(
    offers: Seq[Offer],
    pids: Seq[String]) extends ProtoWrapper[Messages.ResourceOffersMessage] {
  def toProto: Messages.ResourceOffersMessage =
    Messages.ResourceOffersMessage
      .newBuilder
      .addAllOffers(offers.map(_.toProto).asJava)
      .addAllPids(pids.asJava)
      .build()
}

object ResourceOffersMessage {
  def fromBytes(bytes: Array[Byte]): ResourceOffersMessage =
    ResourceOffersMessage(Messages.ResourceOffersMessage.parseFrom(bytes))

  def apply(proto: Messages.ResourceOffersMessage): ResourceOffersMessage =
    ResourceOffersMessage(
      offers = proto.getOffersList.asScala.map(Offer(_)),
      pids = proto.getPidsList.asScala)
}
