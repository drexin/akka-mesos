package akka.mesos.protos.internal

import mesos.internal.Messages

final case class RoleInfo(
    name: String,
    weight: Option[Double]) {
  def toProto: Messages.RoleInfo = {
    val builder = Messages.RoleInfo
      .newBuilder
      .setName(name)

    weight.foreach(builder.setWeight)

    builder.build()
  }
}
