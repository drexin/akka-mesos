package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.JavaConverters._

final case class Credentials(credentials: Seq[Credential]) {
  def toProto: Protos.Credentials =
    Protos.Credentials
      .newBuilder
      .addAllCredentials(credentials.map(_.toProto).asJava)
      .build()
}
