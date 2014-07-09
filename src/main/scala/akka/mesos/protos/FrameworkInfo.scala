package akka.mesos.protos

import org.apache.mesos.Protos.{ FrameworkInfo => PBFrameworkInfo }
import scala.concurrent.duration._

case class FrameworkInfo(
  name: String,
  user: String,
  id: Option[FrameworkID] = None,
  failoverTimeout: Option[Duration] = None,
  checkpoint: Option[Boolean] = None,
  role: Option[String] = None
) {
  def toProto: PBFrameworkInfo = {
    val builder = PBFrameworkInfo.newBuilder
      .setName(name)
      .setUser(user)

    id.foreach(x => builder.setId(x.toProtos))
    failoverTimeout.foreach(d => builder.setFailoverTimeout(d.toMillis.toDouble / 1000))
    checkpoint.foreach(builder.setCheckpoint)
    role.foreach(builder.setRole)

    builder.build()
  }
}
