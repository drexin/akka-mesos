package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck.Properties

import scala.util.Success

object SlaveIDSpecification extends Properties("SlaveID") {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (id: String) =>
    val proto = Protos.SlaveID.newBuilder.setValue(id).build()

    SlaveID(id).toProto == proto
  }

  property("apply") = forAll { (id: String) =>
    val proto = Protos.SlaveID.newBuilder.setValue(id).build()

    SlaveID(proto) == SlaveID(id)
  }

  property("fromBytes") = forAll { (id: String) =>
    val proto = Protos.SlaveID.newBuilder.setValue(id).build()

    SlaveID.fromBytes(proto.toByteArray) == Success(SlaveID(id))
  }
}
