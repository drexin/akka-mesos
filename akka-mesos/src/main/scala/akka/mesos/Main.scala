package akka.mesos

import akka.libprocess.PID
import akka.mesos.protos._
import akka.mesos.scheduler.{ SchedulerDriver, Scheduler }
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.collection.immutable.Seq
import scala.concurrent.duration._

class MyScheduler(_driver: SchedulerDriver) extends Scheduler(_driver) {

  val log = LoggerFactory.getLogger(getClass)
  var taskCounter: Int = 0

  override def registered(frameworkId: FrameworkID, masterInfo: MasterInfo): Unit = {
    log.info(s"Framework $frameworkId registered with master ${masterInfo.id}")
  }

  override def offerRescinded(offerId: OfferID): Unit = {

  }

  override def disconnected(): Unit = {

  }

  override def reregistered(masterInfo: MasterInfo): Unit = {

  }

  override def slaveLost(slaveId: SlaveID): Unit = {
    log.info(s"Lost slave: ${slaveId.value}")
  }

  override def error(message: String): Unit = {}

  override def frameworkMessage(executorId: ExecutorID, slaveId: SlaveID, data: ByteString): Unit = {}

  override def statusUpdate(status: TaskStatus): Unit = {
    log.info(s"Task ${status.taskId.value} is now ${status.state}")
  }

  override def resourceOffers(offers: Seq[Offer]): Unit = {
    offers foreach { offer =>
      log.info(s"Starting sleep task with offer: ${offer.id}")
      val task = TaskInfo(
        name = "sleep",
        taskId = TaskID(s"sleep-$taskCounter"),
        slaveId = offer.slaveId,
        offer.resources.filter(_.tpe != ResourceType.PORTS),
        command = Some(
          CommandInfo(
            value = "sleep 1"
          )
        )
      )

      taskCounter += 1
      driver.launchTasks(Seq(task -> offer.id))
    }
  }

  override def executorLost(executorId: ExecutorID, slaveId: SlaveID, status: Int): Unit = {
    log.info(s"Lost executor: ${executorId.value}")
  }
}

object Main extends App {
  import akka.actor._

  val config = ConfigFactory.load()

  val system = ActorSystem("Mesos", config)

  val framework = FrameworkInfo(
    name = "foo",
    user = "corpsi",
    failoverTimeout = Some(5.minutes),
    checkpoint = Some(true)
  )

  Mesos(system).registerFramework(PID("127.0.0.1", 5050, "master"), framework, driver => new MyScheduler(driver))
}
