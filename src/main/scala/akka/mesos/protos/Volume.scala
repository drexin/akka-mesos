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
  sealed trait Mode {
    def toProto: Protos.Volume.Mode
  }

  object Mode {
    case object RW extends Mode {
      def toProto = Protos.Volume.Mode.RW
    }

    case object RO extends Mode {
      def toProto = Protos.Volume.Mode.RO
    }
  }
}
