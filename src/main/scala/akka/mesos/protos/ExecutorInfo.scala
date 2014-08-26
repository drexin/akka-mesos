package akka.mesos.protos

import akka.util.ByteString
import org.apache.mesos.Protos.{ ExecutorInfo => PBExecutorInfo }
import scala.collection.JavaConverters._
import com.google.protobuf.{ ByteString => PBByteString }

case class ExecutorInfo(
    executorID: ExecutorID,
    command: CommandInfo,
    resources: Seq[Resource],
    frameworkID: Option[FrameworkID] = None,
    name: Option[String],
    source: Option[String],
    data: Option[ByteString],
    container: Option[ContainerInfo]) {
  def toProto: PBExecutorInfo = {
    val builder =
      PBExecutorInfo
        .newBuilder
        .setExecutorId(executorID.toProto)
        .setCommand(command.toProto)
        .addAllResources(resources.map(_.toProto).asJava)

    frameworkID.foreach(id => builder.setFrameworkId(id.toProto))
    name.foreach(builder.setName)
    source.foreach(builder.setSource)
    data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.asByteBuffer)))
    container.foreach(c => builder.setContainer(c.toProto))

    builder.build()
  }
}
