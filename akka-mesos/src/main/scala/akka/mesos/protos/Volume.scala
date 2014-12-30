package akka.mesos.protos

import org.apache.mesos.Protos

import scala.util.Try

final case class Volume(containerPath: String, mode: Volume.Mode, hostPath: Option[String] = None) extends ProtoWrapper[Protos.Volume] {
  def toProto: Protos.Volume = {
    val builder = Protos.Volume
      .newBuilder()
      .setContainerPath(containerPath)
      .setMode(mode.toProto)

    hostPath.foreach(builder.setHostPath)
    builder.build()
  }
}

object Volume extends ProtoReads[Volume] {

  def apply(proto: Protos.Volume): Volume = {
    val hostPath = if (proto.hasHostPath) Some(proto.getHostPath) else None
    Volume(
      proto.getContainerPath,
      Mode(proto.getMode),
      hostPath)
  }

  sealed trait Mode {
    def toProto: Protos.Volume.Mode
  }

  object Mode {

    def apply(proto: Protos.Volume.Mode): Mode = proto match {
      case Protos.Volume.Mode.RO => RO
      case Protos.Volume.Mode.RW => RW
    }

    case object RW extends Mode {
      def toProto = Protos.Volume.Mode.RW
    }

    case object RO extends Mode {
      def toProto = Protos.Volume.Mode.RO
    }
  }

  override def fromBytes(bytes: Array[Byte]): Try[Volume] = Try {
    Volume(Protos.Volume.parseFrom(bytes))
  }
}
