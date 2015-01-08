package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck.{ Arbitrary, Gen, Properties }

object TaskStateSpecification extends Properties("TaskState") {
  import org.scalacheck.Prop.forAll

  val taskStateGen = {
    import Protos.TaskState._

    Gen.oneOf(TASK_ERROR, TASK_FAILED, TASK_FINISHED, TASK_KILLED, TASK_LOST, TASK_RUNNING, TASK_STAGING, TASK_STARTING)
  }

  implicit lazy val arbTaskState = Arbitrary(taskStateGen)

  property("apply and toProto") = forAll { (proto: Protos.TaskState) =>
    TaskState(proto).toProto == proto
  }
}
