package akka.mesos

import akka.libprocess.PID
import akka.mesos.protos._
import akka.mesos.protos.internal.{ StatusUpdateAcknowledgementMessage, ResourceOffersMessage }
import akka.mesos.scheduler.SchedulerPublisher.{ StatusUpdate, ResourceOffers }
import akka.mesos.scheduler.{ Scheduler, SchedulerDriver }
import akka.stream.FlowMaterializer
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.collection.immutable.Seq
import scala.concurrent.duration._
import scala.util.Success

class MyScheduler(_driver: SchedulerDriver) extends Scheduler(_driver) {

  val log = LoggerFactory.getLogger(getClass)
  var taskCounter: Int = 0
  def TaskMem = 16.0
  def TaskCPUs = 0.1

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

      val memCount = offer.resources.collectFirst {
        case ScalarResource(tpe, value, _) if tpe == ResourceType.MEM => (value / TaskMem).toInt
      }.getOrElse(0)

      val cpuCount = offer.resources.collectFirst {
        case ScalarResource(tpe, value, _) if tpe == ResourceType.CPUS => (value / TaskCPUs).toInt
      }.getOrElse(0)

      val taskCount = memCount min cpuCount

      val tasks = for (_ <- 0 until taskCount) yield {
        val task = TaskInfo(
          name = "sleep",
          taskId = TaskID(s"sleep-$taskCounter"),
          slaveId = offer.slaveId,
          Seq(ScalarResource(ResourceType.MEM, TaskMem), ScalarResource(ResourceType.CPUS, TaskCPUs)),
          command = Some(
            CommandInfo(
              value = "sleep 10"
            )
          )
        )
        taskCounter += 1
        task
      }
      driver.launchTasks(tasks, Seq(offer.id))
    }
  }

  override def executorLost(executorId: ExecutorID, slaveId: SlaveID, status: Int): Unit = {
    log.info(s"Lost executor: ${executorId.value}")
  }
}

object Main extends App {
  import akka.actor._

  val config = ConfigFactory.load()
  val log = LoggerFactory.getLogger("Main")
  var taskCounter: Int = 0
  val TaskMem = 16.0
  val TaskCPUs = 0.1

  implicit val system = ActorSystem("Mesos", config)

  val frameworkInfo = FrameworkInfo(
    name = "foo",
    user = "corpsi",
    failoverTimeout = Some(5.minutes),
    checkpoint = Some(true)
  )

  val framework = Mesos(system).registerFramework(Success(PID("127.0.0.1", 5050, "master")), frameworkInfo)

  implicit val materializer = FlowMaterializer()
  framework.schedulerMessages.foreach {
    case ResourceOffers(offers) =>
      offers foreach { offer =>
        log.info(s"Starting sleep task with offer: ${offer.id}")

        val memCount = offer.resources.collectFirst {
          case ScalarResource(tpe, value, _) if tpe == ResourceType.MEM => (value / TaskMem).toInt
        }.getOrElse(0)

        val cpuCount = offer.resources.collectFirst {
          case ScalarResource(tpe, value, _) if tpe == ResourceType.CPUS => (value / TaskCPUs).toInt
        }.getOrElse(0)

        val taskCount = memCount min cpuCount

        val tasks = for (_ <- 0 until taskCount) yield {
          val task = TaskInfo(
            name = "sleep",
            taskId = TaskID(s"sleep-$taskCounter"),
            slaveId = offer.slaveId,
            Seq(ScalarResource(ResourceType.MEM, TaskMem), ScalarResource(ResourceType.CPUS, TaskCPUs)),
            command = Some(
              CommandInfo(
                value = "sleep 10"
              )
            )
          )
          taskCounter += 1
          task
        }
        framework.driver.launchTasks(tasks, Seq(offer.id))
      }

    case StatusUpdate(update) =>
      println(s"Task ${update.status.taskId} id now ${update.status.state}")
      framework.driver.acknowledgeStatusUpdate(update)

    case x => println(s"Received $x")
  }
}
