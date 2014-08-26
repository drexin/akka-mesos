package akka.mesos.protos

import java.net.{ InetAddress, Inet4Address }

import org.apache.mesos.Protos

final case class MasterInfo(
    id: String,
    ip: Inet4Address,
    port: Int,
    pid: Option[String] = None,
    hostname: Option[String] = None) {

  def toProto: Protos.MasterInfo = {
    val builder = Protos.MasterInfo
      .newBuilder
      .setId(id)
      .setIp(ipToInt(ip))
      .setPort(port)

    pid.foreach(builder.setPid)
    hostname.foreach(builder.setHostname)

    builder.build()
  }

  private[protos] def ipToInt(ip: Inet4Address): Int = {
    ip.getAddress.reverse.zipWithIndex.foldLeft(0) {
      case (res, (b, i)) =>
        ((b.toInt & 0xFF) << i * 8) | res
    }
  }
}

object MasterInfo {
  private[protos] def intToIp(i: Int): Inet4Address = {
    val bytes = Array(((i >> 24) & 0xFF).toByte, ((i >> 16) & 0xFF).toByte, ((i >> 8) & 0xFF).toByte, (i & 0xFF).toByte)
    InetAddress.getByAddress(bytes).asInstanceOf[Inet4Address]
  }
}
