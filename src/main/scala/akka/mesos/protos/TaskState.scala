package akka.mesos.protos

import org.apache.mesos.Protos

sealed trait TaskState {
  def toProto: Protos.TaskState
}

object TaskState {

  case object TaskStaging extends TaskState {
    def toProto: Protos.TaskState = Protos.TaskState.TASK_STAGING
  }

  case object TaskStarting extends TaskState {
    def toProto: Protos.TaskState = Protos.TaskState.TASK_STARTING
  }

  case object TaskRunning extends TaskState {
    def toProto: Protos.TaskState = Protos.TaskState.TASK_RUNNING
  }

  case object TaskFinished extends TaskState {
    def toProto: Protos.TaskState = Protos.TaskState.TASK_FINISHED
  }

  case object TaskFailed extends TaskState {
    def toProto: Protos.TaskState = Protos.TaskState.TASK_FAILED
  }

  case object TaskKilled extends TaskState {
    def toProto: Protos.TaskState = Protos.TaskState.TASK_KILLED
  }

  case object TaskLost extends TaskState {
    def toProto: Protos.TaskState = Protos.TaskState.TASK_LOST
  }

  def apply(state: Protos.TaskState): TaskState = state match {
    case Protos.TaskState.TASK_STAGING  => TaskStaging
    case Protos.TaskState.TASK_STARTING => TaskStarting
    case Protos.TaskState.TASK_RUNNING  => TaskRunning
    case Protos.TaskState.TASK_FINISHED => TaskFinished
    case Protos.TaskState.TASK_FAILED   => TaskFailed
    case Protos.TaskState.TASK_KILLED   => TaskKilled
    case Protos.TaskState.TASK_LOST     => TaskLost
  }
}
