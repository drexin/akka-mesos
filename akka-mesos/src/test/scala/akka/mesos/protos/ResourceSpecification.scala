package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck.Properties

import scala.collection.JavaConverters._
import scala.collection.immutable._
import scala.util.Success

object RangesResourceSpecification extends Properties("RangesResource") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (resourceType: ResourceType, ranges: Seq[NumericRange[Long]], role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.RANGES)
      .setName(resourceType.toString)
      .setRanges(Protos.Value.Ranges
        .newBuilder()
        .addAllRange(ranges.map { x =>
          Protos.Value.Range
            .newBuilder
            .setBegin(x.start)
            .setEnd(x.end)
            .build()
        }.asJava))

    role.foreach(builder.setRole)

    val proto = builder.build()

    RangesResource(resourceType, ranges, role).toProto == proto
  }

  property("apply") = forAll { (resourceType: ResourceType, ranges: Seq[NumericRange[Long]], role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.RANGES)
      .setName(resourceType.toString)
      .setRanges(Protos.Value.Ranges
        .newBuilder()
        .addAllRange(ranges.map { x =>
          Protos.Value.Range
            .newBuilder
            .setBegin(x.start)
            .setEnd(x.end)
            .build()
        }.asJava))

    role.foreach(builder.setRole)

    val proto = builder.build()

    Resource(proto) == RangesResource(resourceType, ranges, role)
  }

  property("fromBytes") = forAll { (resourceType: ResourceType, ranges: Seq[NumericRange[Long]], role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.RANGES)
      .setName(resourceType.toString)
      .setRanges(Protos.Value.Ranges
        .newBuilder()
        .addAllRange(ranges.map { x =>
          Protos.Value.Range
            .newBuilder
            .setBegin(x.start)
            .setEnd(x.end)
            .build()
        }.asJava))

    role.foreach(builder.setRole)

    val proto = builder.build()

    Resource(proto.toByteArray) == Success(RangesResource(resourceType, ranges, role))
  }
}

object ScalarResourceSpecification extends Properties("ScalarResource") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (resourceType: ResourceType, scalar: Double, role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.SCALAR)
      .setName(resourceType.toString)
      .setScalar(Protos.Value.Scalar.newBuilder.setValue(scalar))

    role.foreach(builder.setRole)

    val proto = builder.build()

    ScalarResource(resourceType, scalar, role).toProto == proto
  }

  property("apply") = forAll { (resourceType: ResourceType, scalar: Double, role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.SCALAR)
      .setName(resourceType.toString)
      .setScalar(Protos.Value.Scalar.newBuilder.setValue(scalar))

    role.foreach(builder.setRole)

    val proto = builder.build()

    Resource(proto) == ScalarResource(resourceType, scalar, role)
  }

  property("fromBytes") = forAll { (resourceType: ResourceType, scalar: Double, role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.SCALAR)
      .setName(resourceType.toString)
      .setScalar(Protos.Value.Scalar.newBuilder.setValue(scalar))

    role.foreach(builder.setRole)

    val proto = builder.build()

    Resource(proto.toByteArray) == Success(ScalarResource(resourceType, scalar, role))
  }
}

object SetResourceSpecification extends Properties("SetResource") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (resourceType: ResourceType, set: Set[String], role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.SET)
      .setName(resourceType.toString)
      .setSet(Protos.Value.Set
        .newBuilder()
        .addAllItem(set.asJava))

    role.foreach(builder.setRole)

    val proto = builder.build()

    SetResource(resourceType, set, role).toProto == proto
  }

  property("apply") = forAll { (resourceType: ResourceType, set: Set[String], role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.SET)
      .setName(resourceType.toString)
      .setSet(Protos.Value.Set
        .newBuilder()
        .addAllItem(set.asJava))

    role.foreach(builder.setRole)

    val proto = builder.build()

    Resource(proto) == SetResource(resourceType, set, role)
  }

  property("fromBytes") = forAll { (resourceType: ResourceType, set: Set[String], role: Option[String]) =>
    val builder = Protos.Resource
      .newBuilder()
      .setType(Protos.Value.Type.SET)
      .setName(resourceType.toString)
      .setSet(Protos.Value.Set
        .newBuilder()
        .addAllItem(set.asJava))

    role.foreach(builder.setRole)

    val proto = builder.build()

    Resource(proto.toByteArray) == Success(SetResource(resourceType, set, role))
  }
}
