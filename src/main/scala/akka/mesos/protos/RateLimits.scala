package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.JavaConverters._

final case class RateLimits(
    limits: Seq[RateLimit],
    aggregateDefaultCapacity: Option[Long] = None,
    aggregateDefaultQps: Option[Double] = None) {
  def toProto: Protos.RateLimits = {
    val builder = Protos.RateLimits
      .newBuilder
      .addAllLimits(limits.map(_.toProto).asJava)

    aggregateDefaultCapacity.foreach(builder.setAggregateDefaultCapacity)
    aggregateDefaultQps.foreach(builder.setAggregateDefaultQps)

    builder.build()
  }
}
