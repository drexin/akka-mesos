package akka.mesos.protos.internal

import mesos.internal.Messages

final case class SubmitSchedulerResponse(okay: Boolean) {
  def toProto: Messages.SubmitSchedulerResponse =
    Messages.SubmitSchedulerResponse.newBuilder.setOkay(okay).build()
}
