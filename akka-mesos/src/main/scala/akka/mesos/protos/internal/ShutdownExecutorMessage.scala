package akka.mesos.protos.internal

import akka.mesos.protos.{ ProtoReads, ProtoWrapper }
import mesos.internal.Messages

import scala.util.Try

case class ShutdownExecutorMessage() extends ProtoWrapper[Messages.ShutdownExecutorMessage] {
  def toProto: Messages.ShutdownExecutorMessage =
    Messages.ShutdownExecutorMessage
      .newBuilder
      .build()
}

object ShutdownExecutorMessage extends ProtoReads[ShutdownExecutorMessage] {
  override def fromBytes(bytes: Array[Byte]): Try[ShutdownExecutorMessage] = Try {
    Messages.ShutdownExecutorMessage.parseFrom(bytes)
    ShutdownExecutorMessage()
  }
}
