package akka.mesos.protos

import akka.util.ByteString
import org.apache.mesos.Protos
import scala.collection.JavaConverters._
import com.google.protobuf.{ ByteString => PBByteString }

import scala.util.Try

final case class ExecutorInfo(
    executorId: ExecutorID,
    command: CommandInfo,
    resources: Seq[Resource] = Nil,
    frameworkId: Option[FrameworkID] = None,
    name: Option[String] = None,
    source: Option[String] = None,
    data: Option[ByteString] = None,
    container: Option[ContainerInfo] = None) extends ProtoWrapper[Protos.ExecutorInfo] {
  def toProto: Protos.ExecutorInfo = {
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

    builder.build()
  }
}

object ExecutorInfo extends ProtoReads[ExecutorInfo] {
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

  override def fromBytes(bytes: Array[Byte]): Try[ExecutorInfo] = Try {
    ExecutorInfo(Protos.ExecutorInfo.parseFrom(bytes))
  }
}
