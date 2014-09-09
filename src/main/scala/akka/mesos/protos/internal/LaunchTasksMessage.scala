package akka.mesos.protos.internal

import akka.mesos.protos._
import mesos.internal.Messages

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._

final case class LaunchTasksMessage(
    frameworkId: FrameworkID,
    tasksToOfferIds: Seq[(TaskInfo, OfferID)],
    filters: Filters) extends ProtoWrapper[Messages.LaunchTasksMessage] {
  def toProto: Messages.LaunchTasksMessage = {
    val (tasks, offerIds) = tasksToOfferIds.unzip

    Messages.LaunchTasksMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .addAllTasks(tasks.map(_.toProto).asJava)
      .addAllOfferIds(offerIds.map(_.toProto).asJava)
      .setFilters(filters.toProto)
      .build()
  }
}

object LaunchTasksMessage {

  def fromBytes(bytes: Array[Byte]): LaunchTasksMessage =
    LaunchTasksMessage(Messages.LaunchTasksMessage.parseFrom(bytes))

  def apply(proto: Messages.LaunchTasksMessage): LaunchTasksMessage = {
    val taskInfos = proto.getTasksList.asScala.map(TaskInfo(_))
    val offerIds = proto.getOfferIdsList.asScala.map(OfferID(_))

    val tasksToOfferIds = taskInfos zip offerIds

    LaunchTasksMessage(
      FrameworkID(proto.getFrameworkId),
      tasksToOfferIds.to[Seq],
      Filters(proto.getFilters)
    )
  }
}
