package akka.mesos.protos.internal

import mesos.internal.Messages

final case class ProjdReadyMessage(project: String) {
  def toProto: Messages.ProjdReadyMessage =
    Messages.ProjdReadyMessage
      .newBuilder
      .setProject(project)
      .build()
}
