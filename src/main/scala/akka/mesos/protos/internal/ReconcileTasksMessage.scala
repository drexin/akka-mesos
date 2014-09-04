package akka.mesos.protos.internal

import akka.mesos.protos.{ TaskStatus, FrameworkID }
import mesos.internal.Messages

import scala.collection.JavaConverters._

class ReconcileTasksMessage(
    frameworkId: FrameworkID,
    statuses: Seq[TaskStatus]) {
  def toProto: Messages.ReconcileTasksMessage =
    Messages.ReconcileTasksMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .addAllStatuses(statuses.map(_.toProto).asJava)
      .build()
}
