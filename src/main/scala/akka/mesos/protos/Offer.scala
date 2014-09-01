package akka.mesos.protos

import org.apache.mesos.Protos.{ Offer => PBOffer }
import scala.collection.JavaConverters._

final case class Offer(
    id: OfferID,
    slaveID: SlaveID,
    hostname: String,
    resources: Seq[Resource],
    attributes: Seq[Attribute],
    executorIDs: Seq[ExecutorID]) {
  def toProto: PBOffer =
    PBOffer
      .newBuilder
      .setId(id.toProto)
      .setSlaveId(slaveID.toProto)
      .setHostname(hostname)
      .addAllResources(resources.map(_.toProto).asJava)
      .addAllAttributes(attributes.map(_.toProto).asJava)
      .addAllExecutorIds(executorIDs.map(_.toProto).asJava)
      .build()
}
