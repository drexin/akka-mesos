package akka.mesos.protos

import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import org.apache.mesos.Protos
import org.scalacheck.Properties

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
//import scala.util.Success

object TaskInfoSpecification extends Properties("TaskInfo") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll {
    (name: String,
    taskId: TaskID,
    slaveId: SlaveID,
    resources: Seq[Resource],
    executor: Option[ExecutorInfo],
    command: Option[CommandInfo],
    data: Option[ByteString],
    healthCheck: Option[HealthCheck]) =>
      val builder =
        Protos.TaskInfo
          .newBuilder
          .setName(name)
          .setTaskId(taskId.toProto)
          .setSlaveId(slaveId.toProto)
          .addAllResources(resources.map(_.toProto).asJava)

      executor.foreach(e => builder.setExecutor(e.toProto))
      command.foreach(c => builder.setCommand(c.toProto))
      data.foreach(bs => builder.setData(PBByteString.copyFrom(bs.toArray)))
      healthCheck.foreach(hs => builder.setHealthCheck(hs.toProto))

      val proto = builder.build()

      TaskInfo(name, taskId, slaveId, resources, executor, command, data, healthCheck).toProto == proto
  }

  /*
    property("apply") = forAll { (id: String) =>
      val proto = Protos.SlaveID.newBuilder.setValue(id).build()

      SlaveID(proto) == SlaveID(id)
    }

    property("fromBytes") = forAll { (id: String) =>
      val proto = Protos.SlaveID.newBuilder.setValue(id).build()

      SlaveID.fromBytes(proto.toByteArray) == Success(SlaveID(id))
    }*/

}
