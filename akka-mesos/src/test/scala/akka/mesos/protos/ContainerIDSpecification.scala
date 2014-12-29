package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck._

import scala.util.Success

object ContainerIDSpecification extends Properties("ContainerID") {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (id: String) =>
    ContainerID(id).toProto == Protos.ContainerID.newBuilder.setValue(id).build()
  }

  property("apply") = forAll { (id: String) =>
    val proto = Protos.ContainerID.newBuilder.setValue(id).build()
    ContainerID(proto) == ContainerID(id)
  }

  property("fromBytes") = forAll { (id: String) =>
    val proto = Protos.ContainerID.newBuilder.setValue(id).build()
    ContainerID.fromBytes(proto.toByteArray) == Success(ContainerID(id))
  }
}
