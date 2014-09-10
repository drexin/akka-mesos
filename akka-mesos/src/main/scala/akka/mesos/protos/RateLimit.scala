package akka.mesos.protos

import org.apache.mesos.Protos

final case class RateLimit(
    principal: String,
    capacity: Option[Long],
    qps: Option[Double] = None) {
  def toProto: Protos.RateLimit = {
    val builder = Protos.RateLimit
      .newBuilder
      .setPrincipal(principal)

    capacity.foreach(builder.setCapacity)
    qps.foreach(builder.setQps)

    builder.build()
  }
}
