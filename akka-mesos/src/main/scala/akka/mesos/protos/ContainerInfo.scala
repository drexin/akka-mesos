package akka.mesos.protos

import akka.mesos.protos.DockerContainerInfo.{ PortMapping, Network }
import org.apache.mesos.Protos

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._
import scala.util.Try

class UnknownContainerTypeException(tpe: Protos.ContainerInfo.Type) extends Exception(s"Unknown container type '${tpe.name}'")

sealed trait ContainerInfo extends ProtoWrapper[Protos.ContainerInfo] {
  def volumes: Seq[Volume]
  def hostname: Option[String]
}

object ContainerInfo extends ProtoReads[ContainerInfo] {
  def apply(proto: Protos.ContainerInfo): ContainerInfo = proto.getType match {
    case Protos.ContainerInfo.Type.DOCKER =>
      val docker = proto.getDocker
      DockerContainerInfo(
        docker.getImage,
        if (proto.hasHostname) Some(proto.getHostname) else None,
        proto.getVolumesList.asScala.map(Volume(_)).to[Seq],
        if (docker.hasNetwork) Some(Network(docker.getNetwork)) else None,
        docker.getPortMappingsList.asScala.map(PortMapping(_)).to[Seq],
        if (docker.hasPrivileged) Some(docker.getPrivileged) else None,
        docker.getParametersList.asScala.map(Parameter(_)).to[Seq],
        if (docker.hasForcePullImage) Some(docker.getForcePullImage) else None)

    case x => throw new UnknownContainerTypeException(x)
  }

  sealed trait Type {
    def toProto: Protos.ContainerInfo.Type
  }

  object Type {
    case object Docker extends Type {
      override def toProto: Protos.ContainerInfo.Type = Protos.ContainerInfo.Type.DOCKER
    }
  }

  override def fromBytes(bytes: Array[Byte]): Try[ContainerInfo] = Try {
    ContainerInfo(Protos.ContainerInfo.parseFrom(bytes))
  }
}

object DockerContainerInfo {
  sealed trait Network {
    def toProto: Protos.ContainerInfo.DockerInfo.Network
  }

  object Network {
    import Protos.ContainerInfo.DockerInfo.Network._

    case object Host extends Network {
      override def toProto: Protos.ContainerInfo.DockerInfo.Network = HOST
    }

    case object Bridge extends Network {
      override def toProto: Protos.ContainerInfo.DockerInfo.Network = BRIDGE
    }

    case object None extends Network {
      override def toProto: Protos.ContainerInfo.DockerInfo.Network = NONE
    }

    def apply(proto: Protos.ContainerInfo.DockerInfo.Network): Network = proto match {
      case HOST   => Host
      case BRIDGE => Bridge
      case NONE   => None
    }
  }

  object PortMapping extends ProtoReads[PortMapping] {
    def apply(proto: Protos.ContainerInfo.DockerInfo.PortMapping): PortMapping = {
      PortMapping(
        proto.getHostPort,
        proto.getContainerPort,
        if (proto.hasProtocol) Some(proto.getProtocol) else None
      )
    }

    override def fromBytes(bytes: Array[Byte]): Try[PortMapping] = ???
  }

  case class PortMapping(hostPort: Int, containerPort: Int, protocol: Option[String] = None) extends ProtoWrapper[Protos.ContainerInfo.DockerInfo.PortMapping] {
    override def toProto: Protos.ContainerInfo.DockerInfo.PortMapping = {
      val builder = Protos.ContainerInfo.DockerInfo.PortMapping
        .newBuilder
        .setHostPort(hostPort)
        .setContainerPort(containerPort)

      protocol.foreach(builder.setProtocol)

      builder.build()
    }
  }
}

final case class DockerContainerInfo(
    image: String,
    hostname: Option[String] = None,
    volumes: Seq[Volume] = Nil,
    network: Option[Network] = None,
    portMappings: Seq[PortMapping] = Nil,
    privileged: Option[Boolean] = None,
    parameters: Seq[Parameter] = Nil,
    forcePullImage: Option[Boolean] = None) extends ContainerInfo {
  override def toProto: Protos.ContainerInfo = {
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

    builder.build()
  }
}
