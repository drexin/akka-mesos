package akka.mesos.protos

import mesos.internal.Messages
import org.apache.mesos.Protos

final case class DeclineResourceOfferMessage(frameworkId: FrameworkID, offerId: OfferID, filters: Filters) extends ProtoWrapper[Messages.LaunchTasksMessage] {
  def toProto: Messages.LaunchTasksMessage =
    Messages.LaunchTasksMessage
      .newBuilder
      .setFrameworkId(frameworkId.toProto)
      .addOfferIds(offerId.toProto)
      .setFilters(filters.toProto)
      .build()
}
