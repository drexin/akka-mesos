package akka.mesos.stream

import akka.actor.{ Actor, ActorRef, Stash }
import akka.libprocess.LibProcessMessage
import akka.mesos.MesosFrameworkActor.Disconnected
import akka.mesos.protos.internal._
import akka.mesos.protos._
import akka.mesos.stream.SchedulerDriverActor.{ FrameworkConnected, SchedulerDriverMessage }
import akka.util.ByteString

import scala.collection.immutable.Seq

object SchedulerDriverActor {
  sealed trait SchedulerDriverMessage {
    def withFrameworkId(id: FrameworkID): ProtoWrapper[_]
  }

  case object UnregisterFramework extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = UnregisterFrameworkMessage(id)
  }

  case object DeactivateFramework extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = DeactivateFrameworkMessage(id)
  }

  case class ResourceRequest(requests: Seq[Request]) extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = ResourceRequestMessage(id, requests)
  }

  case class LaunchTasks(tasks: Seq[TaskInfo], offers: Seq[OfferID], filters: Filters) extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = LaunchTasksMessage(id, tasks, offers, filters)
  }

  case class KillTask(taskId: TaskID) extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = KillTaskMessage(id, taskId)
  }

  case class DeclineResourceOffer(offerId: OfferID, filters: Filters) extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = DeclineResourceOfferMessage(id, offerId, filters)
  }

  case object ReviveOffers extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = ReviveOffersMessage(id)
  }

  case class FrameworkMessage(slaveId: SlaveID, executorId: ExecutorID, data: ByteString) extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = FrameworkToExecutorMessage(slaveId, id, executorId, data)
  }

  case class ReconcileTasks(statuses: Seq[TaskStatus]) extends SchedulerDriverMessage {
    override def withFrameworkId(id: FrameworkID): ProtoWrapper[_] = ReconcileTasksMessage(id, statuses)
  }

  case class FrameworkConnected(masterRef: ActorRef, frameworkId: FrameworkID)
}

class SchedulerDriverActor(frameworkName: String) extends Actor with Stash {
  var masterRef: ActorRef = _
  var frameworkId: FrameworkID = _

  override def receive = disconnected

  def disconnected: Receive = {
    case FrameworkConnected(ref, id) =>
      masterRef = ref
      frameworkId = id
      context.become(ready)
      unstashAll()

    case _ => stash()
  }

  def ready: Receive = {
    case msg: SchedulerDriverMessage =>
      masterRef ! LibProcessMessage(frameworkName, msg.withFrameworkId(frameworkId))

    case Disconnected =>
      context.become(disconnected)
  }
}
