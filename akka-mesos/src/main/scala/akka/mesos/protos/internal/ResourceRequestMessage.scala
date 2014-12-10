package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper, Request, FrameworkID }
import mesos.internal.Messages

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
import scala.util.Try

final case class ResourceRequestMessage(
    frameworkId: FrameworkID,
    requests: Seq[Request]) extends ProtoWrapper[Messages.ResourceRequestMessage] {
  def toProto: Messages.ResourceRequestMessage =
    Messages.ResourceRequestMessage
      .newBuilder
      .addAllRequests(requests.map(_.toProto).asJava)
      .build()
}

object ResourceRequestMessage extends ProtoReads[ResourceRequestMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[ResourceRequestMessage] = Try {
    ResourceRequestMessage(Messages.ResourceRequestMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.ResourceRequestMessage): ResourceRequestMessage =
    ResourceRequestMessage(
      FrameworkID(proto.getFrameworkId),
      proto.getRequestsList.asScala.map(Request(_)).to[Seq]
    )
}
