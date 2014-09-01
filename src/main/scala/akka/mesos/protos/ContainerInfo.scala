package akka.mesos.protos

import org.apache.mesos.Protos
import scala.collection.JavaConverters._

sealed trait ContainerInfo {
  def volumes: Seq[Volume]

  def toProto: Protos.ContainerInfo
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
