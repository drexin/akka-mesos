package akka.mesos.protos

import akka.util.ByteString
import org.apache.mesos.Protos
import scala.collection.JavaConverters._
import com.google.protobuf.{ ByteString => PBByteString }

final case class ExecutorInfo(
    executorID: ExecutorID,
    command: CommandInfo,
    resources: Seq[Resource],
    frameworkId: Option[FrameworkID] = None,
    name: Option[String],
    source: Option[String],
    data: Option[ByteString],
    container: Option[ContainerInfo]) extends ProtoWrapper[Protos.ExecutorInfo] {
  def toProto: Protos.ExecutorInfo = {
    val builder =
      Protos.ExecutorInfo
        .newBuilder
        .setExecutorId(executorID.toProto)
        .setCommand(command.toProto)
        .addAllResources(resources.map(_.toProto).asJava)

    frameworkId.foreach(id => builder.setFrameworkId(id.toProto))
    name.foreach(builder.setName)
    source.foreach(builder.setSource)
    data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.asByteBuffer)))
    container.foreach(c => builder.setContainer(c.toProto))

    builder.build()
  }
}

object ExecutorInfo {
  def apply(proto: Protos.ExecutorInfo): ExecutorInfo = {
    val frameworkId = if (proto.hasFrameworkId) Some(FrameworkID(proto.getFrameworkId)) else None
    val name = if (proto.hasName) Some(proto.getName) else None
    val source = if (proto.hasSource) Some(proto.getSource) else None
    val data = if (proto.hasData) Some(ByteString(proto.getData.toByteArray)) else None
    val container = if (proto.hasContainer) Some(ContainerInfo(proto.getContainer)) else None

    ExecutorInfo(
      ExecutorID(proto.getExecutorId),
      CommandInfo(proto.getCommand),
      proto.getResourcesList.asScala.map(Resource(_)),
      frameworkId,
      name,
      source,
      data,
      container
    )
  }
}
