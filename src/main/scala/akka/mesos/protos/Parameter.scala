package akka.mesos.protos

import org.apache.mesos.Protos

final case class Parameter(key: String, value: String) {
  def toProto: Protos.Parameter =
    Protos.Parameter
      .newBuilder
      .setKey(key)
      .setValue(value)
      .build()
}
