package akka.mesos.protos.internal

import mesos.internal.Messages

case object ShutdownExecutorMessage {
  def toProto: Messages.ShutdownExecutorMessage =
    Messages.ShutdownExecutorMessage
      .newBuilder
      .build()
}
