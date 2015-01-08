package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck.Properties

import scala.util.Success

class TaskIDSpecification extends Properties("TaskID") {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (id: String) =>
    val proto = Protos.TaskID.newBuilder.setValue(id).build()

    TaskID(id).toProto == proto
  }

  property("apply") = forAll { (id: String) =>
    val proto = Protos.TaskID.newBuilder.setValue(id).build()

    TaskID(proto) == TaskID(id)
  }

  property("fromBytes") = forAll { (id: String) =>
    val proto = Protos.TaskID.newBuilder.setValue(id).build()

    TaskID.fromBytes(proto.toByteArray) == Success(TaskID(id))
  }
}
