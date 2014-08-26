package akka.mesos.protos

import org.apache.mesos.Protos
import scala.collection.JavaConverters._

case class SlaveInfo(
    hostname: String,
    port: Option[Int] = None,
    resources: Seq[Resource] = Nil,
    attributes: Seq[Attribute] = Nil,
    id: Option[SlaveID] = None,
    checkpoint: Boolean = false) {
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
