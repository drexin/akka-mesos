/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import java.net.InetSocketAddress

import akka.actor._
import akka.io.IO
import akka.libprocess.serde.MessageSerDe
import spray.can.Http

private[libprocess] class LibProcessHTTPEndpoint(address: String, port: Int, messageSerDe: MessageSerDe, manager: ActorRef) extends Actor with ActorLogging {
  import akka.libprocess.LibProcessHTTPEndpoint._
  import context.system

  override def preStart(): Unit = {
    IO(Http) ! Http.Bind(self, address, port)
  }

  override def postStop(): Unit = {
    IO(Http) ! Http.Unbind
  }

  def receive = {
    case Http.Bound(addr) =>
      log.info(s"Successfully bound Http endpoint to ${addr}.")
      context.become(ready)
      manager ! Started(addr)

    case Http.CommandFailed(cmd: Http.Bind) =>
      log.error(s"Failed to bind Http endpoint to ${cmd.endpoint}.")
      context.stop(self)
  }

  def ready: Receive = {
    case Http.Connected(remote, _) =>
      log.info(s"Incoming connection from $remote")
      val handler = context.actorOf(Props(classOf[LibProcessHTTPHandler], messageSerDe))
      sender() ! Http.Register(handler)
  }
}

private[libprocess] object LibProcessHTTPEndpoint {
  case class Started(address: InetSocketAddress)
}
