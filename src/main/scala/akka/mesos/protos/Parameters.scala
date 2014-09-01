package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.JavaConverters._

final case class Parameters(parameters: Seq[Parameter]) {
  def toProto: Protos.Parameters =
    Protos.Parameters
      .newBuilder
      .addAllParameter(parameters
        .map(_.toProto)
        .asJava).build()
}
