package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.JavaConverters._

class Request(
    slaveId: Option[SlaveID] = None,
    resources: Seq[Resource] = Nil) {
  def toProto: Protos.Request = {
    val builder = Protos.Request.newBuilder

    slaveId.foreach(x => builder.setSlaveId(x.toProto))
    builder.addAllResources(resources.map(_.toProto).asJava)

    builder.build()
  }
}
