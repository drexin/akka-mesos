package akka.mesos.protos.internal

import mesos.internal.Messages

import scala.collection.JavaConverters._

final case class AuthenticationMechanismsMessage(mechanisms: Seq[String]) {
  def toProto: Messages.AuthenticationMechanismsMessage =
    Messages.AuthenticationMechanismsMessage
      .newBuilder
      .addAllMechanisms(mechanisms.asJava)
      .build()
}
