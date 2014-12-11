package akka.mesos.executor

import akka.mesos.protos.TaskStatus
import akka.util.ByteString

abstract class ExecutorDriver private[mesos] () {
  def stop(): Unit
  def abort(): Unit
  def sendStatusUpdate(status: TaskStatus): Unit
  def sendFrameworkMessage(data: ByteString): Unit
}
