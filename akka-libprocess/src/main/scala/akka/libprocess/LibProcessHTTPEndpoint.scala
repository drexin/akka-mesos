/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import java.net.InetSocketAddress

import akka.actor._
import akka.http.Http
import akka.http.model.{ HttpMethods, HttpRequest, HttpResponse, Uri }
import akka.libprocess.LibProcessManager.LibProcessInternalMessage
import akka.libprocess.serde.{ MessageSerDe, TransportMessage }
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{ Flow, Sink }

import scala.util.{ Failure, Success, Try }

private[libprocess] class LibProcessHTTPEndpoint(address: String, port: Int, messageSerDe: MessageSerDe, manager: ActorRef) extends Actor with ActorLogging {
  import akka.libprocess.LibProcessHTTPEndpoint._
  import context.dispatcher

  implicit val materializer = ActorFlowMaterializer()

  val pathRegex = "/([^/]+)/(.+)".r

  override def preStart(): Unit = {
    val binding = Http(context.system).bind(address, port)
    val mm = binding.startHandlingWith(Flow[HttpRequest].map(handle))
    binding.localAddress(mm).foreach(manager ! Started(_))
  }

  def receive = PartialFunction.empty

  val handle: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.POST, Uri.Path(pathRegex(receiverName, cls)), headers, entity, _) =>
      entity.dataBytes.map { data =>
        val msgResult = messageSerDe.deserialize(TransportMessage(cls.toString, data))

        val pidResult = Try {
          headers.find(_.lowercaseName == "libprocess-from").get.value()
        }.flatMap(PID.fromAddressString)
        val internalMessage = for {
          msg <- msgResult
          pid <- pidResult
        } yield LibProcessInternalMessage(pid, receiverName.toString, msg)

        internalMessage match {
          case Success(im) =>
            manager ! im
          case Failure(t) =>
            log.error(t, s"Failed to parse message of type $cls")
        }
      }.runWith(Sink.ignore)

      HttpResponse(200)

    case _ => HttpResponse(404)
  }
}

private[libprocess] object LibProcessHTTPEndpoint {
  case class Started(address: InetSocketAddress)
}
