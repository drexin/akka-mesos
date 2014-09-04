package akka.mesos.serde

import akka.libprocess.serde.{ MessageSerDe, TransportMessage }
import com.typesafe.config.Config
import scala.util.{ Success, Failure, Try }
import com.google.protobuf.MessageLite
import mesos.internal.Messages._

class ProtobufSerDe(config: Config) extends MessageSerDe {
  def deserialize(message: TransportMessage): Try[AnyRef] = {
    constructorMapping.get(message.messageName) map { ctor =>
      try {
        val msg = ctor(message.data)
        Success(msg)
      } catch {
        case e: Exception =>
          println(e.getMessage)
          e.printStackTrace
          Failure(e)
      }
    } getOrElse {
      Failure(new NoSuchElementException)
    }
  }

  def serialize(obj: AnyRef): Try[TransportMessage] = obj match {
    case msg: MessageLite =>
      typeMapping.get(msg.getClass) map { name =>
        Success(TransportMessage(name, msg.toByteArray))
      } getOrElse {
        Failure(new NoSuchElementException)
      }

    case _ =>
      Failure(new ClassCastException)
  }

  val constructorMapping: Map[String, Array[Byte] => MessageLite] = Map(
    "mesos.internal.FrameworkRegisteredMessage" -> FrameworkRegisteredMessage.parseFrom,
    "mesos.internal.ResourceOffersMessage" -> ResourceOffersMessage.parseFrom
  )

  val typeMapping: Map[Class[_], String] = Map(
    classOf[RegisterFrameworkMessage] -> "mesos.internal.RegisterFrameworkMessage",
    classOf[RescindResourceOfferMessage] -> "mesos.internal.RescindResourceOfferMessage"
  )
}

