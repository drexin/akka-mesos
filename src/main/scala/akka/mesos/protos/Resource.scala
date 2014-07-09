package akka.mesos.protos

import org.apache.mesos.Protos.{ Resource => PBResource, Value => PBValue }
import PBValue.{ Scalar => PBScalar, Range => PBRange, Ranges => PBRanges, Set => PBSet }
import scala.collection.JavaConverters._

sealed trait Resource {
  def tpe: ResourceType
  def role: Option[String]

  def toProtos: PBResource
}

case class ScalarResource(
  tpe: ResourceType,
  value: Double,
  role: Option[String] = None
) extends Resource {
  def toProtos: PBResource = {
    val builder =
      PBResource
        .newBuilder
        .setName(tpe.toString)
        .setScalar(PBScalar.newBuilder.setValue(value).build())
        .setType(PBValue.Type.SCALAR)

    role.foreach(builder.setRole)

    builder.build()
  }
}

case class RangesResource(
  tpe: ResourceType,
  value: Seq[Range],
  role: Option[String] = None
) extends Resource {
  def toProtos: PBResource = {
    val builder =
      PBResource
        .newBuilder
        .setName(tpe.toString)
        .setRanges(ranges)
        .setType(PBValue.Type.SCALAR)

    role.foreach(builder.setRole)

    builder.build()
  }

  private final def ranges = {
    PBRanges
      .newBuilder
      .addAllRange(
        value.map { range =>
          PBRange
            .newBuilder
            .setBegin(range.start)
            .setEnd(range.end)
            .build()
        }.asJava
      )
      .build()
  }
}

case class SetResource(
  tpe: ResourceType,
  value: Seq[String],
  role: Option[String] = None
) extends Resource {
  def toProtos: PBResource = {
    val builder =
      PBResource
        .newBuilder
        .setName(tpe.toString)
        .setSet(PBSet.newBuilder.addAllItem(value.asJava).build())
        .setType(PBValue.Type.SCALAR)

    role.foreach(builder.setRole)

    builder.build()
  }
}

sealed trait ResourceType

object ResourceType {

  case object CPUS extends ResourceType {
    override def toString() = "cpus"
  }

  case object MEM extends ResourceType {
    override def toString() = "mem"
  }

  case object PORTS extends ResourceType {
    override def toString() = "ports"
  }

  case object DISK extends ResourceType {
    override def toString() = "disk"
  }

  def fromString(tpe: String): ResourceType = tpe.toLowerCase match {
    case "cpus"   => CPUS
    case "mem"    => MEM
    case "ports"  => PORTS
    case "disk"   => DISK
  }

  def apply(tpe: String): ResourceType = fromString(tpe)
}
