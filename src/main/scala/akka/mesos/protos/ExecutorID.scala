package akka.mesos.protos

import org.apache.mesos.Protos.{ ExecutorID => PBExecutorID }

class ExecutorID(value: String) {
  def toProto: PBExecutorID =
    PBExecutorID
      .newBuilder
      .setValue(value)
      .build()
}
