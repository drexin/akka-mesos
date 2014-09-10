/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import akka.actor._
import akka.libprocess.LibProcessManager.LibProcessInternalMessage
import akka.libprocess.serde.{ MessageSerDe, TransportMessage }
import spray.can.Http
import spray.http.{ HttpResponse, HttpMethods, HttpRequest, Uri }

import scala.util.{ Try, Failure, Success }

private[libprocess] class LibProcessHTTPHandler(messageSerDe: MessageSerDe) extends Actor with ActorLogging {
  val manager = LibProcess(context.system).manager

  val pathRegex = "/([^/]+)/(.+)".r
  val pidRegex = "libprocess/(.+)".r

  override def receive = {
    case HttpRequest(HttpMethods.POST, Uri.Path(pathRegex(receiverName, cls)), headers, entity, _) =>
      val msgResult = messageSerDe.deserialize(TransportMessage(cls.toString, entity.data.toByteArray))

      val pidResult = Try {
        val pidRegex(addressString) = headers.find(_.lowercaseName == "user-agent").get.value
        addressString
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
      sender() ! HttpResponse(200)

    case _: Http.CloseCommand =>
      log.info("Received close")
      context.stop(self)
  }
}
