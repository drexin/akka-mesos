/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import akka.actor._
import com.typesafe.config.Config
import akka.libprocess.LibProcessHTTPEndpoint.Started
import java.net.InetSocketAddress
import akka.libprocess.serde.MessageSerDe
import akka.actor.SupervisorStrategy.Restart
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit

import scala.util.control.NonFatal

class LibProcessConfig(config: Config) {
  val port: Int = config.getInt("port")
  val address: String = config.getString("address")

  val messageSerDe: String = config.getString("message-serde")
  val serDeConfig: Config = config.getConfig(messageSerDe)

  val connectionRetries: Int = config.getInt("connection-retries")
  val retryTimeout: Duration = config.getDuration("retry-timeout", TimeUnit.MILLISECONDS).millis
}

class LibProcessManager(config: LibProcessConfig) extends Actor with ActorLogging with Stash {
  import LibProcessManager._

  val messageSerDe = createMessageSerDe

  override def preStart() {
    context.watch {
      context.actorOf(Props(classOf[LibProcessHTTPEndpoint], config.address, config.port, messageSerDe, self))
    }
  }

  var registry = Map.empty[String, ActorRef]
  var remoteRefCache = Map.empty[PID, ActorRef]
  var localAddress: InetSocketAddress = _

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(config.connectionRetries, config.retryTimeout) {
    case NonFatal(_) => Restart
  }

  def receive = {
    case Started(addr) =>
      localAddress = addr
      context.become(ready)
      unstashAll()

    case _ => stash()
  }

  def ready: Receive = {
    case Register(name, ref) =>
      if (registry.contains(name)) {
        sender ! RegistrationFailed(name)
      } else {
        registry += name -> ref
        sender ! Registered(name)
      }

    case Unregister(name) =>
      registry -= name
      sender() ! Unregistered(name)

    case LibProcessInternalMessage(pid, receiverName, msg) =>
      registry.get(receiverName) match {
        case Some(ref) =>
          retrieveRemoteRef(pid, None) foreach { remoteRef =>
            ref.tell(msg, remoteRef)
          }

        case None => log.warning(s"Received message for unregistered actor '$receiverName'.")
      }

    case GetRemoteRef(pid) => retrieveRemoteRef(pid, Some(sender()))
    case Terminated(ref) =>
      remoteRefCache = remoteRefCache.filterNot(_._2 == ref)
      context.unwatch(ref)

  }

  def retrieveRemoteRef(pid: PID, receiver: Option[ActorRef]): Option[ActorRef] = {
    remoteRefCache.get(pid) match {
      case Some(ref) => Some(ref)
      case None =>
        try {
          val ref = context.actorOf(Props(classOf[LibProcessRemoteActor], receiver, pid.id, new InetSocketAddress(pid.ip, pid.port), localAddress, messageSerDe))
          context.watch(ref)
          remoteRefCache += pid -> ref
          Some(ref)
        } catch {
          case e: Exception =>
            log.error(e, s"Could not create RemoteActor for PID '${pid.toAddressString}'")
            None
        }
    }
  }

  def createMessageSerDe: MessageSerDe = {
    val cls = Class.forName(config.serDeConfig.getString("fqcn"), true, getClass.getClassLoader)

    try {
      cls.getConstructor(classOf[Config]).newInstance(config.serDeConfig).asInstanceOf[MessageSerDe]
    } catch {
      case e: NoSuchMethodException =>
        cls.newInstance().asInstanceOf[MessageSerDe]
    }
  }
}

object LibProcessManager {
  final case class Register(name: String, ref: ActorRef)
  final case class Unregister(name: String)
  final case class LibProcessInternalMessage(sender: PID, receiverName: String, msg: AnyRef)
  final case class GetRemoteRef(pid: PID)

  final case class RegistrationFailed(name: String)
  final case class Registered(name: String)
  final case class Unregistered(name: String)
  final case class RetrievalFailed(pid: PID)

  final case class RemoteRef(ref: ActorRef)
}

final case class LibProcessMessage(sender: String, msg: AnyRef)
