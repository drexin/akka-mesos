package akka.mesos.protos.internal

import mesos.internal.Messages

final case class RegisterProjdMessage(project: String) {
  def toProto: Messages.RegisterProjdMessage =
    Messages.RegisterProjdMessage
      .newBuilder
      .setProject(project)
      .build()
}
