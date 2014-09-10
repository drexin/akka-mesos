package akka.mesos.protos.internal

import akka.mesos.protos.{ Request, FrameworkID }
import mesos.internal.Messages

import scala.collection.JavaConverters._

final case class ResourceRequestMessage(
    frameworkId: FrameworkID,
    requests: Seq[Request]) {
  def toProto: Messages.ResourceRequestMessage =
    Messages.ResourceRequestMessage
      .newBuilder
      .addAllRequests(requests.map(_.toProto).asJava)
      .build()
}
