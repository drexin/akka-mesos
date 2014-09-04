package akka.mesos.protos.internal

import akka.mesos.protos.{ ExecutorInfo, SlaveID, SlaveInfo }
import mesos.internal.Messages

import scala.collection.JavaConverters._

final case class ReregisterSlaveMessage(
    slaveId: SlaveID,
    slave: SlaveInfo,
    executorInfos: Seq[ExecutorInfo],
    tasks: Seq[Task],
    completedFrameworks: Seq[Archive.Framework]) {
  def toProto: Messages.ReregisterSlaveMessage =
    Messages.ReregisterSlaveMessage
      .newBuilder
      .setSlaveId(slaveId.toProto)
      .setSlave(slave.toProto)
      .addAllExecutorInfos(executorInfos.map(_.toProto).asJava)
      .addAllTasks(tasks.map(_.toProto).asJava)
      .addAllCompletedFrameworks(completedFrameworks.map(_.toProto).asJava)
      .build()
}
