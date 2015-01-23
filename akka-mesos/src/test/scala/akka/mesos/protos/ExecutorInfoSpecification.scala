package akka.mesos.protos

import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import org.apache.mesos.Protos
import org.scalacheck.Properties

import scala.collection.JavaConverters._
import scala.util.Success

object ExecutorInfoSpecification extends Properties("ExecutorInfo") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll {
    (executorId: ExecutorID,
    command: CommandInfo,
    resources: Seq[Resource],
    frameworkId: Option[FrameworkID],
    name: Option[String],
    source: Option[String],
    data: Option[ByteString],
    container: Option[ContainerInfo]) =>
      val builder =
        Protos.ExecutorInfo
          .newBuilder
          .setExecutorId(executorId.toProto)
          .setCommand(command.toProto)
          .addAllResources(resources.map(_.toProto).asJava)

      frameworkId.foreach(id => builder.setFrameworkId(id.toProto))
      name.foreach(builder.setName)
      source.foreach(builder.setSource)
      data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.asByteBuffer)))
      container.foreach(c => builder.setContainer(c.toProto))

      val proto = builder.build()

      ExecutorInfo(executorId, command, resources, frameworkId, name, source, data, container).toProto == proto
  }

  property("apply") = forAll {
    (executorId: ExecutorID,
    command: CommandInfo,
    resources: Seq[Resource],
    frameworkId: Option[FrameworkID],
    name: Option[String],
    source: Option[String],
    data: Option[ByteString],
    container: Option[ContainerInfo]) =>
      val builder =
        Protos.ExecutorInfo
          .newBuilder
          .setExecutorId(executorId.toProto)
          .setCommand(command.toProto)
          .addAllResources(resources.map(_.toProto).asJava)

      frameworkId.foreach(id => builder.setFrameworkId(id.toProto))
      name.foreach(builder.setName)
      source.foreach(builder.setSource)
      data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.asByteBuffer)))
      container.foreach(c => builder.setContainer(c.toProto))

      val proto = builder.build()

      ExecutorInfo(proto) == ExecutorInfo(executorId, command, resources, frameworkId, name, source, data, container)
  }

  property("fromBytes") = forAll {
    (executorId: ExecutorID,
    command: CommandInfo,
    resources: Seq[Resource],
    frameworkId: Option[FrameworkID],
    name: Option[String],
    source: Option[String],
    data: Option[ByteString],
    container: Option[ContainerInfo]) =>
      val builder =
        Protos.ExecutorInfo
          .newBuilder
          .setExecutorId(executorId.toProto)
          .setCommand(command.toProto)
          .addAllResources(resources.map(_.toProto).asJava)

      frameworkId.foreach(id => builder.setFrameworkId(id.toProto))
      name.foreach(builder.setName)
      source.foreach(builder.setSource)
      data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.asByteBuffer)))
      container.foreach(c => builder.setContainer(c.toProto))

      val proto = builder.build()

      ExecutorInfo(proto.toByteArray) == Success(ExecutorInfo(executorId, command, resources, frameworkId, name, source, data, container))
  }
}
