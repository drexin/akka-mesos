/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.libprocess.LibProcessManager.GetRemoteRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

object LibProcess extends ExtensionId[LibProcessExtension] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): LibProcessExtension =
    new LibProcessExtension(system)

  override def lookup(): ExtensionId[_ <: Extension] = this
}

class LibProcessExtension(system: ExtendedActorSystem) extends Extension {
  val config = system.settings.config.getConfig("akka.libprocess")
  val manager = system.actorOf(Props(classOf[LibProcessManager], new LibProcessConfig(config)))

  def remoteRef(pid: PID): Future[ActorRef] = {
    implicit val timeout: Timeout = config.getDuration("timeout", TimeUnit.MILLISECONDS).millis
    (manager ? GetRemoteRef(pid)).mapTo[ActorRef]
  }
}
