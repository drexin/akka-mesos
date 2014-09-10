package akka.mesos.protos.internal

import mesos.internal.Messages

final case class SubmitSchedulerRequest(name: String) {
  def toProto: Messages.SubmitSchedulerRequest =
    Messages.SubmitSchedulerRequest.newBuilder.setName(name).build()
}
