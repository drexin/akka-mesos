package akka.mesos.protos

import org.apache.mesos.Protos.{ TaskID => PBTaskID }

case class TaskID(value: String) {
  def toProto: PBTaskID =
    PBTaskID
      .newBuilder
      .setValue(value)
      .build()
}
