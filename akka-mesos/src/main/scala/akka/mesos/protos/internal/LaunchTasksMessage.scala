package akka.mesos.protos.internal

import akka.mesos.protos._
import mesos.internal.Messages

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._
import scala.util.Try

final case class LaunchTasksMessage(
    frameworkId: FrameworkID,
    tasks: Seq[TaskInfo],
    offers: Seq[OfferID],
    filters: Filters) extends ProtoWrapper[Messages.LaunchTasksMessage] {
  def toProto: Messages.LaunchTasksMessage = {

    Messages.LaunchTasksMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .addAllTasks(tasks.map(_.toProto).asJava)
      .addAllOfferIds(offers.map(_.toProto).asJava)
      .setFilters(filters.toProto)
      .build()
  }
}

object LaunchTasksMessage extends ProtoReads[LaunchTasksMessage] {

  def fromBytes(bytes: Array[Byte]): Try[LaunchTasksMessage] = Try {
    LaunchTasksMessage(Messages.LaunchTasksMessage.parseFrom(bytes))
  }

  def apply(proto: Messages.LaunchTasksMessage): LaunchTasksMessage = {
    val taskInfos = proto.getTasksList.asScala.map(TaskInfo(_)).to[Seq]
    val offers = proto.getOfferIdsList.asScala.map(OfferID(_)).to[Seq]

    LaunchTasksMessage(
      FrameworkID(proto.getFrameworkId),
      taskInfos,
      offers,
      Filters(proto.getFilters)
    )
  }
}
