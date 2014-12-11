package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._
import scala.util.Try

final case class SlaveInfo(
    hostname: String,
    port: Option[Int] = None,
    resources: Seq[Resource] = Nil,
    attributes: Seq[Attribute] = Nil,
    id: Option[SlaveID] = None,
    checkpoint: Boolean = false) extends ProtoWrapper[Protos.SlaveInfo] {
  def toProto: Protos.SlaveInfo = {
    val builder = Protos.SlaveInfo
      .newBuilder
      .setHostname(hostname)
      .setCheckpoint(checkpoint)
      .addAllResources(resources.map(_.toProto).asJava)
      .addAllAttributes(attributes.map(_.toProto).asJava)

    port.foreach(builder.setPort)
    id.foreach(x => builder.setId(x.toProto))

    builder.build()
  }
}

object SlaveInfo extends ProtoReads[SlaveInfo] {
  override def fromBytes(bytes: Array[Byte]): Try[SlaveInfo] = Try {
    SlaveInfo(Protos.SlaveInfo.parseFrom(bytes))
  }

  def apply(proto: Protos.SlaveInfo): SlaveInfo =
    SlaveInfo(
      proto.getHostname,
      if (proto.hasPort) Some(proto.getPort) else None,
      proto.getResourcesList.asScala.map(Resource(_)).to[Seq],
      proto.getAttributesList.asScala.map(Attribute(_)).to[Seq],
      if (proto.hasId) Some(SlaveID(proto.getId)) else None,
      proto.getCheckpoint
    )
}
