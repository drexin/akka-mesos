package akka.mesos.protos

import org.apache.mesos.Protos.{ TaskID => PBTaskID }

case class TaskID(value: String) {
  def toProtos: PBTaskID =
    PBTaskID
      .newBuilder
      .setValue(value)
      .build()
}
