package akka.mesos.protos

import akka.util.ByteString
import org.apache.mesos.Protos.{ ExecutorInfo => PBExecutorInfo }
import scala.collection.JavaConverters._

case class ExecutorInfo(
  executorID: ExecutorID,
  command: CommandInfo,
  resources: Seq[Resource],
  frameworkID: Option[FrameworkID] = None,
  name: Option[String],
  source: Option[String],
  data: Option[ByteString]
) {
  def toProtos: PBExecutorInfo = {
    val builder =
      PBExecutorInfo
        .newBuilder
        .setExecutorId(executorID.toProtos)
        .setCommand(command.toProtos)
        .addAllResources(resources.map(_.toProtos).asJava)

    frameworkID.foreach(id => builder.setFrameworkId(id.toProtos))
    name.foreach(builder.setName)

    builder.build()
  }
}
