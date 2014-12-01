/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import java.net.InetSocketAddress

import akka.actor._
import akka.io.Tcp.SO.KeepAlive
import akka.io.Tcp._
import akka.io.{ IO, Tcp }
import akka.libprocess.LibProcessManager.RemoteRef
import akka.libprocess.serde.{ MessageSerDe, TransportMessage }
import akka.util.ByteString

import scala.util.{ Failure, Success }

class RemoteRefRetrievalException(msg: String) extends Exception(msg)

private[libprocess] final class LibProcessRemoteActor(receiver: Option[ActorRef], name: String, address: InetSocketAddress, localAddress: InetSocketAddress, messageSerDe: MessageSerDe) extends Actor with ActorLogging with Stash {
  import context.system

  var connection: ActorRef = _

  override def preStart(): Unit = {
    IO(Tcp) ! Tcp.Connect(remoteAddress = address, options = KeepAlive(on = true) :: Nil)
  }

  override def postStop(): Unit = {
    if (connection != null)
      connection ! Tcp.Close
  }

  def receive = {
    case Connected(remote, _) =>
      log.info(s"Connected to remote libprocess actor at $remote.")
      connection = sender()
      connection ! Tcp.Register(self)
      receiver.foreach(_ ! RemoteRef(self))
      context.become(ready)
      unstashAll()

    case CommandFailed(cmd: Tcp.Connect) =>
      log.error(s"Failed to connect to remote libprocess at ${cmd.remoteAddress}.")
      receiver.foreach(_ ! Status.Failure(new RemoteRefRetrievalException(s"Could not connect to remote node at ${cmd.remoteAddress}.")))
      context.stop(self)

    case x => stash()
  }

  def ready: Receive = {
    case LibProcessMessage(senderName, msg) =>
      messageSerDe.serialize(msg) match {
        case Success(TransportMessage(msgName, data)) =>
          connection ! Write(
            formatMessage(
              pidWithId(senderName),
              msgName,
              data.toArray
            )
          )

        case Failure(e) => log.error(e, s"Could not send message $msg.")
      }

    case CommandFailed(cmd: Tcp.Write) =>
      log.error(s"Failed to send message to remote actor '$name' at $address.")

    case PeerClosed =>
      log.warning("Connection closed by peer. Shutting down actor.")
      context.stop(self)

    case ErrorClosed(cause) =>
      log.error(s"Connection closed with an error: $cause. Shutting down actor.")
      context.stop(self)
  }

  def pidWithId(id: String): PID = {
    PID(localAddress.getAddress.getHostAddress, localAddress.getPort, id)
  }

  def formatMessage(pid: PID, msgName: String, data: Array[Byte]): ByteString = {
    val builder = ByteString.newBuilder
    log.info(pid.toAddressString)

    builder append ByteString(s"POST /master/$msgName HTTP/1.1\r\n")
    builder append ByteString(s"Libprocess-From: ${pid.toAddressString}\r\n")
    builder append ByteString("Connection: Keep-Alive\r\n")
    builder append ByteString(s"Content-Length: ${data.size}\r\n")
    builder append ByteString(s"Host: ${pid.ip}\r\n")
    builder append ByteString("\r\n")

    builder append ByteString(data)

    builder.result()
  }
}
