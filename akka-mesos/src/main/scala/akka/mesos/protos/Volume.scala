package akka.mesos.protos

import org.apache.mesos.Protos

final case class Volume(containerPath: String, mode: Volume.Mode, hostPath: Option[String] = None) {
  def toProto: Protos.Volume = {
    val builder = Protos.Volume
      .newBuilder()
      .setContainerPath(containerPath)
      .setMode(mode.toProto)

    hostPath.foreach(builder.setHostPath)
    builder.build()
  }
}

object Volume {

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
}
