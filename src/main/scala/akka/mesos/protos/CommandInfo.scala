package akka.mesos.protos

import org.apache.mesos.Protos.{ CommandInfo => PBCommandInfo }
import PBCommandInfo.{
  ContainerInfo => PBContainerInfo,
  URI => PBURI
}

import scala.collection.JavaConverters._

case class CommandInfo(
  uris: Seq[CommandInfo.URI],
  value: String,
  environment: Option[Environment] = None,
  container: Option[CommandInfo.ContainerInfo] = None,
  user: Option[String] = None
) {
  def toProtos: PBCommandInfo = {
    val builder =
      PBCommandInfo
        .newBuilder
        .setValue(value)
        .addAllUris(uris.map(_.toProtos).asJava)

    environment.foreach(e => builder.setEnvironment(e.toProtos))
    container.foreach(c => builder.setContainer(c.toProtos))
    user.foreach(builder.setUser)

    builder.build()
  }
}

object CommandInfo {
  case class ContainerInfo(
    image: String,
    options: Seq[String]
  ) {
    def toProtos: PBContainerInfo =
      PBContainerInfo
        .newBuilder
        .setImage(image)
        .addAllOptions(options.asJava)
        .build()
  }

  case class URI(
    value: String,
    extract: Option[Boolean] = None,
    executable: Option[Boolean] = None
  ) {
    def toProtos: PBURI = {
      val builder = PBURI
        .newBuilder
        .setValue(value)

      extract.foreach(builder.setExtract)
      executable.foreach(builder.setExecutable)

      builder.build()
    }
  }
}

