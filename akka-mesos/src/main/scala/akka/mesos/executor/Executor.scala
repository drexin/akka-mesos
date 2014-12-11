package akka.mesos.executor

import akka.mesos.protos._
import akka.util.ByteString

abstract class Executor {
  def registered(executorInfo: ExecutorInfo, frameworkInfo: FrameworkInfo, slaveInfo: SlaveInfo): Unit
  def reregistered(slaveInfo: SlaveInfo): Unit
  def disconnected(): Unit
  def launchTask(task: TaskInfo): Unit
  def killTask(taskId: TaskID): Unit
  def frameworkMessage(data: ByteString): Unit
  def shutdown(): Unit
  def error(message: String): Unit
}
