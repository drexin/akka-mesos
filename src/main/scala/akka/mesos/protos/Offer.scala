package akka.mesos.protos

import org.apache.mesos.Protos
import scala.collection.JavaConverters._

final case class Offer(
    id: OfferID,
    slaveId: SlaveID,
    hostname: String,
    resources: Seq[Resource],
    attributes: Seq[Attribute],
    executorIds: Seq[ExecutorID]) {
  def toProto: Protos.Offer =
    Protos.Offer
      .newBuilder
      .setId(id.toProto)
      .setSlaveId(slaveId.toProto)
      .setHostname(hostname)
      .addAllResources(resources.map(_.toProto).asJava)
      .addAllAttributes(attributes.map(_.toProto).asJava)
      .addAllExecutorIds(executorIds.map(_.toProto).asJava)
      .build()
}

object Offer {
  def apply(proto: Protos.Offer): Offer =
    Offer(
      id = OfferID(proto.getId),
      slaveId = SlaveID(proto.getSlaveId),
      hostname = proto.getHostname,
      resources = proto.getResourcesList.asScala.map(Resource(_)),
      attributes = proto.getAttributesList.asScala.map(Attribute(_)),
      executorIds = proto.getExecutorIdsList.asScala.map(ExecutorID(_))
    )
}
