package akka.mesos

import akka.actor._
import akka.libprocess._
import akka.mesos.protos._

import scala.util.{ Success, Try }

object Mesos extends ExtensionId[MesosExtension] with ExtensionIdProvider {
  override def lookup(): ExtensionId[_ <: Extension] = this

  override def createExtension(system: ExtendedActorSystem): MesosExtension =
    new MesosExtension(system)
}

class MesosExtension(system: ExtendedActorSystem) extends Extension {
  def registerFramework(master: PID, framework: FrameworkInfo, schedulerCreator: SchedulerDriver => Scheduler): ActorRef = {
    registerFramework(Success(master), framework, schedulerCreator)
  }

  def registerFramework(master: => Try[PID], framework: FrameworkInfo, schedulerCreator: SchedulerDriver => Scheduler): ActorRef = {
    system.actorOf(Props(new MesosFrameworkActor(master, framework, schedulerCreator)))
  }
}
