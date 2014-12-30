package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._

sealed trait ContainerInfo extends ProtoWrapper[Protos.ContainerInfo] {
  def volumes: Seq[Volume]
}

object ContainerInfo {
  def apply(proto: Protos.ContainerInfo): ContainerInfo = proto.getType match {
    case Protos.ContainerInfo.Type.DOCKER =>
      DockerContainerInfo(
        proto.getVolumesList.asScala.map(Volume(_)).to[Seq],
        proto.getDocker.getImage)

    case _ => throw new ClassNotFoundException(s"Unknown container type: ${proto.getType.name()}")
  }

  sealed trait Type {
    def toProto: Protos.ContainerInfo.Type
  }

  object Type {
    case object Docker extends Type {
      override def toProto: Protos.ContainerInfo.Type = Protos.ContainerInfo.Type.DOCKER
    }
  }
}

final case class DockerContainerInfo(volumes: Seq[Volume], image: String) extends ContainerInfo {
  override def toProto: Protos.ContainerInfo =
    Protos.ContainerInfo
      .newBuilder()
      .setType(Protos.ContainerInfo.Type.DOCKER)
      .setDocker(Protos.ContainerInfo.DockerInfo.newBuilder().setImage(image))
      .addAllVolumes(volumes.map(_.toProto).asJava)
      .build()
}
