package akka.mesos.protos

import org.apache.mesos.Protos.{ Offer => PBOffer }
import scala.collection.JavaConverters._

case class Offer(
  id: OfferID,
  slaveID: SlaveID,
  hostname: String,
  resources: Seq[Resource],
  attributes: Seq[Attribute],
  executorIDs: Seq[ExecutorID]
) {
  def toProtos: PBOffer =
    PBOffer
      .newBuilder
      .setId(id.toProtos)
      .setSlaveId(slaveID.toProtos)
      .setHostname(hostname)
      .addAllResources(resources.map(_.toProtos).asJava)
      .addAllAttributes(attributes.map(_.toProtos).asJava)
      .addAllExecutorIds(executorIDs.map(_.toProtos).asJava)
      .build()
}
