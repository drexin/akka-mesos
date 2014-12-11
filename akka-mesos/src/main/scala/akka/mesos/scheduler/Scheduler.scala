package akka.mesos.scheduler

import akka.mesos.protos._
import akka.util.ByteString

import scala.collection.immutable.Seq

abstract class Scheduler(protected val driver: SchedulerDriver) {
  def registered(frameworkId: FrameworkID, masterInfo: MasterInfo): Unit
  def reregistered(masterInfo: MasterInfo): Unit
  def resourceOffers(offers: Seq[Offer]): Unit
  def offerRescinded(offerId: OfferID): Unit
  def statusUpdate(status: TaskStatus): Unit
  def frameworkMessage(executorId: ExecutorID, slaveId: SlaveID, data: ByteString): Unit
  def disconnected(): Unit
  def slaveLost(slaveId: SlaveID): Unit
  def executorLost(executorId: ExecutorID, slaveId: SlaveID, status: Int): Unit
  def error(message: String): Unit
}
