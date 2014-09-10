package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._

final case class CommandInfo(
    value: String,
    uris: Seq[CommandInfo.URI] = Nil,
    environment: Option[Environment] = None,
    container: Option[CommandInfo.ContainerInfo] = None,
    user: Option[String] = None,
    shell: Option[Boolean] = None,
    arguments: Seq[String] = Nil) extends ProtoWrapper[Protos.CommandInfo] {
  def toProto: Protos.CommandInfo = {
    val builder =
      Protos.CommandInfo
        .newBuilder
        .setValue(value)
        .addAllUris(uris.map(_.toProto).asJava)
        .addAllArguments(arguments.asJava)

    environment.foreach(e => builder.setEnvironment(e.toProto))
    container.foreach(c => builder.setContainer(c.toProto))
    user.foreach(builder.setUser)
    shell.foreach(builder.setShell)

    builder.build()
  }
}

object CommandInfo {

  def apply(proto: Protos.CommandInfo): CommandInfo = {
    val environment = if (proto.hasEnvironment) Some(Environment(proto.getEnvironment)) else None
    val container = if (proto.hasContainer) Some(ContainerInfo(proto.getContainer)) else None
    val user = if (proto.hasUser) Some(proto.getUser) else None
    val shell = if (proto.hasShell) Some(proto.getShell) else None

    CommandInfo(
      proto.getValue,
      proto.getUrisList.asScala.map(URI(_)).to[Seq],
      environment,
      container,
      user,
      shell,
      proto.getArgumentsList.asScala.to[Seq]
    )
  }

  final case class ContainerInfo(
      image: String,
      options: Seq[String]) {
    def toProto: Protos.CommandInfo.ContainerInfo =
      Protos.CommandInfo.ContainerInfo
        .newBuilder
        .setImage(image)
        .addAllOptions(options.asJava)
        .build()
  }

  object ContainerInfo {
    def apply(proto: Protos.CommandInfo.ContainerInfo): ContainerInfo =
      ContainerInfo(
        proto.getImage,
        proto.getOptionsList.asScala.to[Seq])
  }

  final case class URI(
      value: String,
      extract: Option[Boolean] = None,
      executable: Option[Boolean] = None) {
    def toProto: Protos.CommandInfo.URI = {
      val builder = Protos.CommandInfo.URI
        .newBuilder
        .setValue(value)

      extract.foreach(builder.setExtract)
      executable.foreach(builder.setExecutable)

      builder.build()
    }
  }

  object URI {
    def apply(proto: Protos.CommandInfo.URI): URI = {
      val extract = if (proto.hasExtract) Some(proto.getExtract) else None
      val executable = if (proto.hasExecutable) Some(proto.getExecutable) else None

      URI(
        proto.getValue,
        extract,
        executable)
    }
  }
}

