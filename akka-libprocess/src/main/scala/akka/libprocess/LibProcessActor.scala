/*
 * Copyright (C) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import akka.actor.{ Stash, ActorLogging, Actor }
import akka.libprocess.LibProcessManager._

trait LibProcessActor extends Actor with ActorLogging with Stash {
  final val manager = LibProcess(context.system).manager
  val name: String

  abstract override def preStart(): Unit = {
    manager ! Register(name, self)
    super.preStart()
  }

  final def receive = {
    case Registered(`name`) =>
      context.become(lpReceive)
      unstashAll()

    case RegistrationFailed(`name`) =>
      log.error(s"Failed to register actor $self with name '$name'.")
      context.stop(self)

    case _ => stash()
  }

  def lpReceive: Receive
}
