package akka.mesos.protos

import org.apache.mesos.Protos

import scala.concurrent.duration.Duration

final class Filters(refuse: Option[Duration]) {
  def toProto: Protos.Filters = {
    val builder = Protos.Filters.newBuilder

    refuse.foreach(x => builder.setRefuseSeconds(x.toMillis / 1000))

    builder.build()
  }
}
