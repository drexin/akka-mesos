/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import java.net.InetSocketAddress

import akka.actor._
import akka.http.Http
import akka.http.model.{ HttpMethods, HttpRequest, HttpResponse, Uri }
import akka.io.IO
import akka.libprocess.LibProcessManager.LibProcessInternalMessage
import akka.libprocess.serde.{ MessageSerDe, TransportMessage }
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{ Try, Failure, Success }

private[libprocess] class LibProcessHTTPEndpoint(address: String, port: Int, messageSerDe: MessageSerDe, manager: ActorRef) extends Actor with ActorLogging {
  import akka.libprocess.LibProcessHTTPEndpoint._

  implicit val materializer = FlowMaterializer()
  implicit val askTimeout: Timeout = 500.millis

  val pathRegex = "/([^/]+)/(.+)".r
  val pidRegex = "libprocess/(.+)".r

  override def preStart(): Unit = {
    IO(Http)(context.system) ! Http.Bind(address, port)
  }

  def receive = {
    case Http.ServerBinding(addr, stream) =>
      log.info(s"Successfully bound Http endpoint to $addr.")
      context.become(ready)
      manager ! Started(addr)
      Source(stream).foreach(self ! _)

    case Http.BindFailedException =>
      log.error(s"Failed to bind Http endpoint to $address:$port.")
      context.stop(self)
  }

  def ready: Receive = {
    case Http.IncomingConnection(remote, publisher, subscriber) =>
      log.info(s"Incoming connection from $remote")
      Source(publisher).map(handle).to(Sink(subscriber)).run()
  }

  def handle(req: HttpRequest): HttpResponse = req match {
    case HttpRequest(HttpMethods.POST, Uri.Path(pathRegex(receiverName, cls)), headers, entity, _) =>
      entity.dataBytes.map { data =>
        val msgResult = messageSerDe.deserialize(TransportMessage(cls.toString, data))

        val pidResult = Try {
          headers.find(_.lowercaseName == "libprocess-from").get.value
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
