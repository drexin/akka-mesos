package akka.mesos.protos.internal

import akka.mesos.protos.Offer
import mesos.internal.Messages

import scala.collection.JavaConverters._

final case class ResourceOffersMessage(
    offers: Seq[Offer],
    pids: Seq[String]) {
  def toProto: Messages.ResourceOffersMessage =
    Messages.ResourceOffersMessage
      .newBuilder
      .addAllOffers(offers.map(_.toProto).asJava)
      .addAllPids(pids.asJava)
      .build()
}
