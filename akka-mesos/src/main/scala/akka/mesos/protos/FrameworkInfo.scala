package akka.mesos.protos

import java.util.concurrent.TimeUnit

import org.apache.mesos.Protos
import org.apache.mesos.Protos.{ FrameworkInfo => PBFrameworkInfo }
import scala.concurrent.duration._

final case class FrameworkInfo(
    name: String,
    user: String,
    id: Option[FrameworkID] = None,
    failoverTimeout: Option[Duration] = None,
    checkpoint: Option[Boolean] = None,
    role: Option[String] = None,
    hostname: Option[String] = None,
    principal: Option[String] = None) extends ProtoWrapper[Protos.FrameworkInfo] {
  def toProto: PBFrameworkInfo = {
    val builder = PBFrameworkInfo.newBuilder
      .setName(name)
      .setUser(user)

    id.foreach(x => builder.setId(x.toProto))
    failoverTimeout.foreach(d => builder.setFailoverTimeout(d.toMillis.toDouble / 1000))
    checkpoint.foreach(builder.setCheckpoint)
    role.foreach(builder.setRole)
    hostname.foreach(builder.setHostname)
    principal.foreach(builder.setPrincipal)

    builder.build()
  }
}

object FrameworkInfo {
  def fromBytes(bytes: Array[Byte]): FrameworkInfo =
    FrameworkInfo(Protos.FrameworkInfo.parseFrom(bytes))

  def apply(proto: Protos.FrameworkInfo): FrameworkInfo = {
    val id = if (proto.hasId) Some(FrameworkID(proto.getId)) else None
    val failoverTimeout = if (proto.hasFailoverTimeout) Some(Duration(proto.getFailoverTimeout, TimeUnit.SECONDS)) else None
    val checkpoint = if (proto.hasCheckpoint) Some(proto.getCheckpoint) else None
    val role = if (proto.hasRole) Some(proto.getRole) else None
    val hostname = if (proto.hasHostname) Some(proto.getHostname) else None
    val principal = if (proto.hasPrincipal) Some(proto.getPrincipal) else None

    FrameworkInfo(proto.getName, proto.getUser, id, failoverTimeout, checkpoint, role, hostname, principal)
  }
}
