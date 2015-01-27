package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq

final case class Offer(
    id: OfferID,
    frameworkId: FrameworkID,
    slaveId: SlaveID,
    hostname: String,
    resources: Seq[Resource],
    attributes: Seq[Attribute],
    executorIds: Seq[ExecutorID]) extends ProtoWrapper[Protos.Offer] {
  def toProto: Protos.Offer =
    Protos.Offer
      .newBuilder
      .setId(id.toProto)
      .setFrameworkId(frameworkId.toProto)
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
      frameworkId = FrameworkID(proto.getFrameworkId),
      slaveId = SlaveID(proto.getSlaveId),
      hostname = proto.getHostname,
      resources = proto.getResourcesList.asScala.to[Seq].map(Resource(_)),
      attributes = proto.getAttributesList.asScala.to[Seq].map(Attribute(_)),
      executorIds = proto.getExecutorIdsList.asScala.to[Seq].map(ExecutorID(_))
    )
}
