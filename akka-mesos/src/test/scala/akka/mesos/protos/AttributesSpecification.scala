package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck.{ Gen, Arbitrary, Properties }

import scala.collection.immutable.{ NumericRange, Seq }
import scala.collection.JavaConverters._
import scala.util.Success

object ScalarAttributeSpecification extends Properties("ScalarAttribute") {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (name: String, scalar: Double) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.SCALAR)
      .setScalar(
        Protos.Value.Scalar
          .newBuilder
          .setValue(scalar)
          .build)
      .build()
    ScalarAttribute(name, scalar).toProto == proto
  }

  property("apply") = forAll { (name: String, scalar: Double) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.SCALAR)
      .setScalar(
        Protos.Value.Scalar
          .newBuilder
          .setValue(scalar)
          .build)
      .build()
    Attribute(proto) == ScalarAttribute(name, scalar)
  }

  property("fromBytes") = forAll { (name: String, scalar: Double) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.SCALAR)
      .setScalar(
        Protos.Value.Scalar
          .newBuilder
          .setValue(scalar)
          .build)
      .build()
    Attribute.fromBytes(proto.toByteArray) == Success(ScalarAttribute(name, scalar))
  }
}

object RangesAttributeSpecification extends Properties("RangesAttribute") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (name: String, ranges: Seq[NumericRange[Long]]) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.RANGES)
      .setRanges(Attribute.Ranges.toProto(ranges))
      .build()
    RangesAttribute(name, ranges).toProto == proto
  }

  property("apply") = forAll { (name: String, ranges: Seq[NumericRange[Long]]) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.RANGES)
      .setRanges(Attribute.Ranges.toProto(ranges))
      .build()
    Attribute(proto) == RangesAttribute(name, ranges)
  }

  property("fromBytes") = forAll { (name: String, ranges: Seq[NumericRange[Long]]) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.RANGES)
      .setRanges(Attribute.Ranges.toProto(ranges))
      .build()
    Attribute.fromBytes(proto.toByteArray) == Success(RangesAttribute(name, ranges))
  }
}

object SetAttributeSpecification extends Properties("SetAttribute") {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (name: String, set: Set[String]) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.SET)
      .setSet(Protos.Value.Set.newBuilder.addAllItem(set.asJava).build())
      .build()
    SetAttribute(name, set).toProto == proto
  }

  property("apply") = forAll { (name: String, set: Set[String]) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.SET)
      .setSet(Protos.Value.Set.newBuilder.addAllItem(set.asJava).build())
      .build()
    Attribute(proto) == SetAttribute(name, set)
  }

  property("fromBytes") = forAll { (name: String, set: Set[String]) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.SET)
      .setSet(Protos.Value.Set.newBuilder.addAllItem(set.asJava).build())
      .build()
    Attribute.fromBytes(proto.toByteArray) == Success(SetAttribute(name, set))
  }
}

object TextAttributeSpecification extends Properties("TextAttribute") {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (name: String, text: String) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.TEXT)
      .setText(
        Protos.Value.Text
          .newBuilder
          .setValue(text)
          .build)
      .build()
    TextAttribute(name, text).toProto == proto
  }

  property("apply") = forAll { (name: String, text: String) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.TEXT)
      .setText(
        Protos.Value.Text
          .newBuilder
          .setValue(text)
          .build)
      .build()
    Attribute(proto) == TextAttribute(name, text)
  }

  property("fromBytes") = forAll { (name: String, text: String) =>
    val proto = Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.TEXT)
      .setText(
        Protos.Value.Text
          .newBuilder
          .setValue(text)
          .build)
      .build()
    Attribute.fromBytes(proto.toByteArray) == Success(TextAttribute(name, text))
  }
}
