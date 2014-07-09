package akka.mesos.protos

import akka.util.ByteString
import org.apache.mesos.Protos.{ TaskInfo => PBTaskInfo }
import com.google.protobuf.{ ByteString => PBByteString }
import scala.collection.JavaConverters._

case class TaskInfo(
  name: String,
  taskID: TaskID,
  slaveID: SlaveID,
  resources: Seq[Resource],
  executor: Option[ExecutorInfo],
  command: Option[CommandInfo],
  data: Option[ByteString],
  healthCheck: Option[HealthCheck]
) {
  def toProtos: PBTaskInfo = {
    val builder =
      PBTaskInfo
        .newBuilder
        .setName(name)
        .setTaskId(taskID.toProtos)
        .setSlaveId(slaveID.toProtos)
        .addAllResources(resources.map(_.toProtos).asJava)

    executor.foreach(e => builder.setExecutor(e.toProtos))
    command.foreach(c => builder.setCommand(c.toProtos))
    data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.toArray)))
    healthCheck.foreach(hs => builder.setHealthCheck(hs.toProtos))

    builder.build()
  }
}

