package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq

final case class Request(
    slaveId: Option[SlaveID] = None,
    resources: Seq[Resource] = Nil) {
  def toProto: Protos.Request = {
    val builder = Protos.Request.newBuilder

    slaveId.foreach(x => builder.setSlaveId(x.toProto))
    builder.addAllResources(resources.map(_.toProto).asJava)

    builder.build()
  }
}

object Request {
  def apply(proto: Protos.Request): Request =
    Request(
      if (proto.hasSlaveId) Some(SlaveID(proto.getSlaveId)) else None,
      proto.getResourcesList.asScala.map(Resource(_)).to[Seq]
    )
}
