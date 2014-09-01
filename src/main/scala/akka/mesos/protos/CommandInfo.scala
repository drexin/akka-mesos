package akka.mesos.protos

import org.apache.mesos.Protos.{ CommandInfo => PBCommandInfo }
import PBCommandInfo.{
  ContainerInfo => PBContainerInfo,
  URI => PBURI
}

import scala.collection.JavaConverters._

final case class CommandInfo(
    uris: Seq[CommandInfo.URI],
    value: String,
    environment: Option[Environment] = None,
    container: Option[CommandInfo.ContainerInfo] = None,
    user: Option[String] = None,
    shell: Boolean = true,
    arguments: Seq[String] = Nil) {
  def toProto: PBCommandInfo = {
    val builder =
      PBCommandInfo
        .newBuilder
        .setValue(value)
        .addAllUris(uris.map(_.toProto).asJava)
        .setShell(shell)
        .addAllArguments(arguments.asJava)

    environment.foreach(e => builder.setEnvironment(e.toProto))
    container.foreach(c => builder.setContainer(c.toProto))
    user.foreach(builder.setUser)

    builder.build()
  }
}

object CommandInfo {
  final case class ContainerInfo(
      image: String,
      options: Seq[String]) {
    def toProto: PBContainerInfo =
      PBContainerInfo
        .newBuilder
        .setImage(image)
        .addAllOptions(options.asJava)
        .build()
  }

  final case class URI(
      value: String,
      extract: Option[Boolean] = None,
      executable: Option[Boolean] = None) {
    def toProto: PBURI = {
      val builder = PBURI
        .newBuilder
        .setValue(value)

      extract.foreach(builder.setExtract)
      executable.foreach(builder.setExecutable)

      builder.build()
    }
  }
}

