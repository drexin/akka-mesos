package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.immutable.{ NumericRange, Seq }
import scala.collection.JavaConverters._
import scala.util.Try

sealed trait Attribute extends ProtoWrapper[Protos.Attribute] {
  def name: String
  def toProto: Protos.Attribute
}

object Attribute extends ProtoReads[Attribute] {
  def apply(proto: Protos.Attribute) = proto.getType match {
    case Protos.Value.Type.SCALAR =>
      ScalarAttribute(proto.getName, proto.getScalar.getValue)

    case Protos.Value.Type.RANGES =>
      RangesAttribute(proto.getName, Ranges(proto.getRanges))

    case Protos.Value.Type.SET =>
      SetAttribute(proto.getName, proto.getSet.getItemList.asScala.toSet)

    case Protos.Value.Type.TEXT =>
      TextAttribute(proto.getName, proto.getText.getValue)
  }

  override def fromBytes(bytes: Array[Byte]): Try[Attribute] = Try {
    Attribute(Protos.Attribute.parseFrom(bytes))
  }

  private[mesos] object Ranges {
    def apply(proto: Protos.Value.Ranges): Seq[NumericRange[Long]] = {
      proto.getRangeList.asScala.map { x =>
        x.getBegin to x.getEnd
      }.to[Seq]
    }

    def toProto(ranges: Seq[NumericRange[Long]]): Protos.Value.Ranges =
      Protos.Value.Ranges
        .newBuilder()
        .addAllRange(ranges.map { x =>
          Protos.Value.Range
            .newBuilder
            .setBegin(x.start)
            .setEnd(x.end)
            .build()
        }.asJava)
        .build()
  }
}

final case class ScalarAttribute(name: String, scalar: Double) extends Attribute {
  override def toProto: Protos.Attribute =
    Protos.Attribute
      .newBuilder
      .setType(Protos.Value.Type.SCALAR)
      .setName(name)
      .setScalar(
        Protos.Value.Scalar
          .newBuilder
          .setValue(scalar)
          .build())
      .build()
}

final case class RangesAttribute(name: String, ranges: Seq[NumericRange[Long]]) extends Attribute {
  override def toProto: Protos.Attribute =
    Protos.Attribute
      .newBuilder
      .setType(Protos.Value.Type.RANGES)
      .setName(name)
      .setRanges(Attribute.Ranges.toProto(ranges))
      .build()
}

final case class SetAttribute(name: String, set: Set[String]) extends Attribute {
  override def toProto: Protos.Attribute =
    Protos.Attribute
      .newBuilder
      .setType(Protos.Value.Type.SET)
      .setName(name)
      .setSet(
        Protos.Value.Set
          .newBuilder
          .addAllItem(set.asJava)
          .build())
      .build()
}

final case class TextAttribute(name: String, text: String) extends Attribute {
  def toProto: Protos.Attribute =
    Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.TEXT)
      .setText(
        Protos.Value.Text
          .newBuilder
          .setValue(text)
          .build())
      .build()
}
