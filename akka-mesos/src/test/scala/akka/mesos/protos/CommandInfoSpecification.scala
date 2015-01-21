package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck.Properties

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
import scala.util.Success

object CommandInfoSpecification extends Properties("CommandInfo") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll {
    (value: String,
    uris: Seq[CommandInfo.URI],
    environment: Option[Environment],
    container: Option[CommandInfo.ContainerInfo],
    user: Option[String],
    shell: Option[Boolean],
    arguments: Seq[String]) =>

      val builder = Protos.CommandInfo
        .newBuilder
        .setValue(value)
        .addAllUris(uris.map(_.toProto).asJava)
        .addAllArguments(arguments.asJava)

      environment.map(_.toProto).foreach(builder.setEnvironment)
      container.map(_.toProto).foreach(builder.setContainer)
      user.foreach(builder.setUser)
      shell.foreach(builder.setShell)

      val proto = builder.build()

      CommandInfo(value, uris, environment, container, user, shell, arguments).toProto == proto
  }

  property("apply") = forAll {
    (value: String,
    uris: Seq[CommandInfo.URI],
    environment: Option[Environment],
    container: Option[CommandInfo.ContainerInfo],
    user: Option[String],
    shell: Option[Boolean],
    arguments: Seq[String]) =>

      val builder = Protos.CommandInfo
        .newBuilder
        .setValue(value)
        .addAllUris(uris.map(_.toProto).asJava)
        .addAllArguments(arguments.asJava)

      environment.map(_.toProto).foreach(builder.setEnvironment)
      container.map(_.toProto).foreach(builder.setContainer)
      user.foreach(builder.setUser)
      shell.foreach(builder.setShell)

      val proto = builder.build()

      CommandInfo(proto) == CommandInfo(value, uris, environment, container, user, shell, arguments)
  }

  property("fromBytes") = forAll {
    (value: String,
    uris: Seq[CommandInfo.URI],
    environment: Option[Environment],
    container: Option[CommandInfo.ContainerInfo],
    user: Option[String],
    shell: Option[Boolean],
    arguments: Seq[String]) =>

      val builder = Protos.CommandInfo
        .newBuilder
        .setValue(value)
        .addAllUris(uris.map(_.toProto).asJava)
        .addAllArguments(arguments.asJava)

      environment.map(_.toProto).foreach(builder.setEnvironment)
      container.map(_.toProto).foreach(builder.setContainer)
      user.foreach(builder.setUser)
      shell.foreach(builder.setShell)

      val proto = builder.build()

      CommandInfo.fromBytes(proto.toByteArray) == Success(CommandInfo(value, uris, environment, container, user, shell, arguments))
  }
}

object CommandInfoContainerInfoSpecification extends Properties("CommandInfo.ContainerInfo") {
  import org.scalacheck.Prop.forAll
  import CommandInfo.ContainerInfo

  property("toProto") = forAll { (image: String, options: Seq[String]) =>
    val proto = Protos.CommandInfo.ContainerInfo
      .newBuilder
      .setImage(image)
      .addAllOptions(options.asJava)
      .build()

    ContainerInfo(image, options).toProto == proto
  }

  property("apply") = forAll { (image: String, options: Seq[String]) =>
    val proto = Protos.CommandInfo.ContainerInfo
      .newBuilder
      .setImage(image)
      .addAllOptions(options.asJava)
      .build()

    ContainerInfo(proto) == ContainerInfo(image, options)
  }

  property("fromBytes") = forAll { (image: String, options: Seq[String]) =>
    val proto = Protos.CommandInfo.ContainerInfo
      .newBuilder
      .setImage(image)
      .addAllOptions(options.asJava)
      .build()

    ContainerInfo.fromBytes(proto.toByteArray) == Success(ContainerInfo(image, options))
  }
}

object CommandInfoURISpecification extends Properties("CommandInfo.URI") {
  import org.scalacheck.Prop.forAll
  import CommandInfo.URI

  property("toProto") = forAll { (uri: String, extract: Option[Boolean], executable: Option[Boolean]) =>
    val builder = Protos.CommandInfo.URI
      .newBuilder
      .setValue(uri)

    extract.foreach(builder.setExtract)
    executable.foreach(builder.setExecutable)

    val proto = builder.build()

    URI(uri, extract, executable).toProto == proto
  }

  property("apply") = forAll { (uri: String, extract: Option[Boolean], executable: Option[Boolean]) =>
    val builder = Protos.CommandInfo.URI
      .newBuilder
      .setValue(uri)

    extract.foreach(builder.setExtract)
    executable.foreach(builder.setExecutable)

    val proto = builder.build()

    URI(proto) == URI(uri, extract, executable)
  }

  property("fromBytes") = forAll { (uri: String, extract: Option[Boolean], executable: Option[Boolean]) =>
    val builder = Protos.CommandInfo.URI
      .newBuilder
      .setValue(uri)

    extract.foreach(builder.setExtract)
    executable.foreach(builder.setExecutable)

    val proto = builder.build()

    URI.fromBytes(proto.toByteArray) == Success(URI(uri, extract, executable))
  }
}
