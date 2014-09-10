package akka.mesos.protos

import java.util.concurrent.TimeUnit

import org.apache.mesos.Protos

import scala.concurrent.duration.Duration

final case class Filters(refuse: Option[Duration]) extends ProtoWrapper[Protos.Filters] {
  def toProto: Protos.Filters = {
    val builder = Protos.Filters.newBuilder

    refuse.foreach(x => builder.setRefuseSeconds(x.toMillis / 1000))

    builder.build()
  }
}

object Filters {
  def apply(proto: Protos.Filters): Filters = {
    val builder = Protos.Filters.newBuilder

    val refuse = if (proto.hasRefuseSeconds) Some(Duration(proto.getRefuseSeconds, TimeUnit.SECONDS)) else None

    Filters(refuse)
  }
}
