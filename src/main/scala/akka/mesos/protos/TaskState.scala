package akka.mesos.protos

import org.apache.mesos.Protos.{ TaskState => PBTaskState }

sealed trait TaskState {
  def toProto: PBTaskState
}

case object TaskStaging extends TaskState {
  def toProto: PBTaskState = PBTaskState.TASK_STAGING
}

case object TaskStarting extends TaskState {
  def toProto: PBTaskState = PBTaskState.TASK_STARTING
}

case object TaskRunning extends TaskState {
  def toProto: PBTaskState = PBTaskState.TASK_RUNNING
}

case object TaskFinished extends TaskState {
  def toProto: PBTaskState = PBTaskState.TASK_FINISHED
}

case object TaskFailed extends TaskState {
  def toProto: PBTaskState = PBTaskState.TASK_FAILED
}

case object TaskKilled extends TaskState {
  def toProto: PBTaskState = PBTaskState.TASK_KILLED
}

case object TaskLost extends TaskState {
  def toProto: PBTaskState = PBTaskState.TASK_LOST
}

