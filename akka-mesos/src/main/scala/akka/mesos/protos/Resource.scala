package akka.mesos.protos

import org.apache.mesos.Protos
import org.apache.mesos.Protos.{ Value => PBValue }
import scala.collection.JavaConverters._
import scala.collection.immutable.NumericRange

sealed trait Resource extends ProtoWrapper[Protos.Resource] {
  def tpe: ResourceType
  def role: Option[String]

  def toProto: Protos.Resource
}

object Resource {
  def apply(proto: Protos.Resource): Resource = {
    val role = if (proto.hasRole) Some(proto.getRole) else None

    proto.getType match {
      case Protos.Value.Type.SCALAR =>
        ScalarResource(
          tpe = ResourceType(proto.getName),
          value = proto.getScalar.getValue,
          role = role
        )

      case Protos.Value.Type.RANGES =>
        RangesResource(
          tpe = ResourceType(proto.getName),
          value = proto.getRanges.getRangeList.asScala.map(x => Range.Long(x.getBegin, x.getEnd, 1)),
          role = role
        )

      case Protos.Value.Type.SET =>
        SetResource(
          tpe = ResourceType(proto.getName),
          value = proto.getSet.getItemList.asScala,
          role = role
        )

      case _ => ???
    }
  }
}

final case class ScalarResource(
    tpe: ResourceType,
    value: Double,
    role: Option[String] = None) extends Resource {
  def toProto: Protos.Resource = {
    val builder =
      Protos.Resource
        .newBuilder
        .setName(tpe.toString)
        .setScalar(Protos.Value.Scalar.newBuilder.setValue(value).build())
        .setType(PBValue.Type.SCALAR)

    role.foreach(builder.setRole)

    builder.build()
  }
}

final case class RangesResource(
    tpe: ResourceType,
    value: Seq[NumericRange[Long]],
    role: Option[String] = None) extends Resource {
  def toProto: Protos.Resource = {
    val builder =
      Protos.Resource
        .newBuilder
        .setName(tpe.toString)
        .setRanges(ranges)
        .setType(PBValue.Type.SCALAR)

    role.foreach(builder.setRole)

    builder.build()
  }

  private def ranges = {
    Protos.Value.Ranges
      .newBuilder
      .addAllRange(
        value.map { range =>
          Protos.Value.Range
            .newBuilder
            .setBegin(range.start)
            .setEnd(range.end)
            .build()
        }.asJava
      )
      .build()
  }
}

final case class SetResource(
    tpe: ResourceType,
    value: Seq[String],
    role: Option[String] = None) extends Resource {
  def toProto: Protos.Resource = {
    val builder =
      Protos.Resource
        .newBuilder
        .setName(tpe.toString)
        .setSet(Protos.Value.Set.newBuilder.addAllItem(value.asJava).build())
        .setType(PBValue.Type.SCALAR)

    role.foreach(builder.setRole)

    builder.build()
  }
}

sealed trait ResourceType

object ResourceType {

  case object CPUS extends ResourceType {
    override def toString = "cpus"
  }

  case object MEM extends ResourceType {
    override def toString = "mem"
  }

  case object PORTS extends ResourceType {
    override def toString = "ports"
  }

  case object DISK extends ResourceType {
    override def toString = "disk"
  }

  def fromString(tpe: String): ResourceType = tpe.toLowerCase match {
    case "cpus"  => CPUS
    case "mem"   => MEM
    case "ports" => PORTS
    case "disk"  => DISK
  }

  def apply(tpe: String): ResourceType = fromString(tpe)
}
