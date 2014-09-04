package akka.mesos.protos.internal

import akka.mesos.protos.Parameters
import mesos.internal.Messages

final case class ProjdUpdateResourcesMessage(parameters: Option[Parameters] = None) {
  def toProto: Messages.ProjdUpdateResourcesMessage = {
    val builder = Messages.ProjdUpdateResourcesMessage.newBuilder

    parameters.foreach(x => builder.setParameters(x.toProto))

    builder.build()
  }
}
