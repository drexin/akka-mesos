package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck.{ Gen, Arbitrary, Properties }

import scala.collection.immutable.{ NumericRange, Seq }
import scala.collection.JavaConverters._
import scala.util.Success

class ScalarAttributeSpecification extends Properties("ScalarAttribute") {
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

class RangesAttributeSpecification extends Properties("RangesAttribute") {
  import org.scalacheck.Prop.forAll

  val longRangeGen: Gen[NumericRange[Long]] = for {
    begin <- Gen.posNum[Long]
    end <- Gen.chooseNum[Long](begin + 1, begin + Int.MaxValue - 1)
  } yield begin.toLong to end

  val longRangeListGen: Gen[Seq[NumericRange[Long]]] = Gen.sized { x =>
    Gen.listOfN(x % Int.MaxValue, longRangeGen)
  }

  implicit lazy val arbLongRange = Arbitrary(longRangeGen)
  implicit lazy val arbLongRangeList = Arbitrary(longRangeListGen)

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

class SetAttributeSpecification extends Properties("SetAttribute") {
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

class TextAttributeSpecification extends Properties("TextAttribute") {
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
