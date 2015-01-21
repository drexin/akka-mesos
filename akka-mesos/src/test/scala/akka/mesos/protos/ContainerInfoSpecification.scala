package akka.mesos.protos

import akka.mesos.protos.DockerContainerInfo.{ PortMapping, Network }
import org.apache.mesos.Protos
import org.scalacheck.Properties

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
import scala.util.Success

object ContainerInfoSpecification extends Properties("ContainerInfo") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll {
    (image: String,
    hostname: Option[String],
    volumes: Seq[Volume],
    network: Option[Network],
    portMappings: Seq[PortMapping],
    privileged: Option[Boolean],
    parameters: Seq[Parameter],
    forcePullImage: Option[Boolean]) =>
      val docker = Protos.ContainerInfo.DockerInfo
        .newBuilder()
        .setImage(image)
        .addAllPortMappings(portMappings.map(_.toProto).asJava)
        .addAllParameters(parameters.map(_.toProto).asJava)

      network.foreach(x => docker.setNetwork(x.toProto))
      privileged.foreach(docker.setPrivileged)
      forcePullImage.foreach(docker.setForcePullImage)

      val builder = Protos.ContainerInfo
        .newBuilder()
        .setType(Protos.ContainerInfo.Type.DOCKER)
        .setDocker(docker)
        .addAllVolumes(volumes.map(_.toProto).asJava)

      hostname.foreach(builder.setHostname)

      val proto = builder.build()

      DockerContainerInfo(image, hostname, volumes, network, portMappings, privileged, parameters, forcePullImage).toProto == proto
  }

  property("apply") = forAll {
    (image: String,
    hostname: Option[String],
    volumes: Seq[Volume],
    network: Option[Network],
    portMappings: Seq[PortMapping],
    privileged: Option[Boolean],
    parameters: Seq[Parameter],
    forcePullImage: Option[Boolean]) =>
      val docker = Protos.ContainerInfo.DockerInfo
        .newBuilder()
        .setImage(image)
        .addAllPortMappings(portMappings.map(_.toProto).asJava)
        .addAllParameters(parameters.map(_.toProto).asJava)

      network.foreach(x => docker.setNetwork(x.toProto))
      privileged.foreach(docker.setPrivileged)
      forcePullImage.foreach(docker.setForcePullImage)

      val builder = Protos.ContainerInfo
        .newBuilder()
        .setType(Protos.ContainerInfo.Type.DOCKER)
        .setDocker(docker)
        .addAllVolumes(volumes.map(_.toProto).asJava)

      hostname.foreach(builder.setHostname)

      val proto = builder.build()

      ContainerInfo(proto) == DockerContainerInfo(image, hostname, volumes, network, portMappings, privileged, parameters, forcePullImage)
  }

  property("fromBytes") = forAll {
    (image: String,
    hostname: Option[String],
    volumes: Seq[Volume],
    network: Option[Network],
    portMappings: Seq[PortMapping],
    privileged: Option[Boolean],
    parameters: Seq[Parameter],
    forcePullImage: Option[Boolean]) =>
      val docker = Protos.ContainerInfo.DockerInfo
        .newBuilder()
        .setImage(image)
        .addAllPortMappings(portMappings.map(_.toProto).asJava)
        .addAllParameters(parameters.map(_.toProto).asJava)

      network.foreach(x => docker.setNetwork(x.toProto))
      privileged.foreach(docker.setPrivileged)
      forcePullImage.foreach(docker.setForcePullImage)

      val builder = Protos.ContainerInfo
        .newBuilder()
        .setType(Protos.ContainerInfo.Type.DOCKER)
        .setDocker(docker)
        .addAllVolumes(volumes.map(_.toProto).asJava)

      hostname.foreach(builder.setHostname)

      val proto = builder.build()

      ContainerInfo.fromBytes(proto.toByteArray) == Success(DockerContainerInfo(image, hostname, volumes, network, portMappings, privileged, parameters, forcePullImage))
  }
}
