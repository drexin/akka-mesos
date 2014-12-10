package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, Offer }
import mesos.internal.Messages

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._
import scala.util.Try

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

object ResourceOffersMessage extends ProtoReads[ResourceOffersMessage] {
  def fromBytes(bytes: Array[Byte]): Try[ResourceOffersMessage] = Try {
    ResourceOffersMessage(Messages.ResourceOffersMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.ResourceOffersMessage): ResourceOffersMessage =
    ResourceOffersMessage(
      offers = proto.getOffersList.asScala.to[Seq].map(Offer(_)),
      pids = proto.getPidsList.asScala.to[Seq])
}
