package akka.mesos.serde

import akka.libprocess.serde.{ MessageSerDe, TransportMessage }
import akka.mesos.protos.internal._
import akka.mesos.protos.{ ProtoReads, DeclineResourceOfferMessage, ProtoWrapper }
import akka.util.ByteString
import com.google.protobuf.MessageLite
import com.typesafe.config.Config

import scala.util.{ Failure, Success, Try }

class ProtobufSerDe(config: Config) extends MessageSerDe {

  def deserialize(message: TransportMessage): Try[AnyRef] = {
    constructorMapping.get(message.messageName) map { ctor =>
      ctor(message.data.toArray)
    } getOrElse {
      Failure(new NoSuchElementException)
    }
  }

  def serialize(obj: AnyRef): Try[TransportMessage] = obj match {
    case msg: ProtoWrapper[_] =>
      typeMapping.get(msg.getClass) map { name =>
        Success(TransportMessage(name, ByteString(msg.toProto.toByteArray)))
      } getOrElse {
        Failure(new NoSuchElementException)
      }

    case _ =>
      Failure(new ClassCastException)
  }

  val constructorMapping: Map[String, ProtoReads[_ <: ProtoWrapper[_]]] = Map(
    "mesos.internal.ExecutorToFrameworkMessage" -> ExecutorToFrameworkMessage,
    "mesos.internal.ExitedExecutorMessage" -> ExitedExecutorMessage,
    "mesos.internal.FrameworkErrorMessage" -> FrameworkErrorMessage,
    "mesos.internal.FrameworkRegisteredMessage" -> FrameworkRegisteredMessage,
    "mesos.internal.FrameworkReregisteredMessage" -> FrameworkReregisteredMessage,
    "mesos.internal.FrameworkToExecutorMessage" -> FrameworkToExecutorMessage,
    "mesos.internal.KillTaskMessage" -> KillTaskMessage,
    "mesos.internal.LostSlaveMessage" -> LostSlaveMessage,
    "mesos.internal.RescindResourceOfferMessage" -> RescindResourceOfferMessage,
    "mesos.internal.ResourceOffersMessage" -> ResourceOffersMessage,
    "mesos.internal.RunTaskMessage" -> RunTaskMessage,
    "mesos.internal.StatusUpdateMessage" -> StatusUpdateMessage
  )

  val typeMapping: Map[Class[_], String] = Map(
    classOf[DeactivateFrameworkMessage] -> "mesos.internal.DeactivateFrameworkMessage",
    classOf[DeclineResourceOfferMessage] -> "mesos.internal.LaunchTasksMessage",
    classOf[FrameworkToExecutorMessage] -> "mesos.internal.FrameworkToExecutorMessage",
    classOf[KillTaskMessage] -> "mesos.internal.KillTaskMessage",
    classOf[LaunchTasksMessage] -> "mesos.internal.LaunchTasksMessage",
    classOf[RegisterFrameworkMessage] -> "mesos.internal.RegisterFrameworkMessage",
    classOf[ReregisterFrameworkMessage] -> "mesos.internal.ReregisterFrameworkMessage",
    classOf[RescindResourceOfferMessage] -> "mesos.internal.RescindResourceOfferMessage",
    classOf[ResourceRequestMessage] -> "mesos.internal.ResourceRequestMessage",
    classOf[ReviveOffersMessage] -> "mesos.internal.ReviveOffersMessage",
    classOf[StatusUpdateAcknowledgementMessage] -> "mesos.internal.StatusUpdateAcknowledgementMessage",
    classOf[UnregisterFrameworkMessage] -> "mesos.internal.UnregisterFrameworkMessage"
  )
}
