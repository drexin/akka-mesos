package akka.mesos.protos.internal

import akka.mesos.protos.{ OfferID, Filters, TaskInfo, FrameworkID }
import mesos.internal.Messages

import scala.collection.JavaConverters._

final case class LaunchTasksMessage(
    frameworkId: FrameworkID,
    tasksToOfferIds: Seq[(TaskInfo, OfferID)],
    filters: Filters) {
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
