package akka.mesos.protos

import akka.util.ByteString
import org.scalacheck.{ Gen, Arbitrary }

trait ProtoGenerators {
  import Arbitrary.arbitrary

  val taskIDGen = for {
    id <- arbitrary[String]
  } yield TaskID(id)

  implicit lazy val arbTaskID: Arbitrary[TaskID] = Arbitrary(taskIDGen)

  val slaveIDGen = for {
    id <- arbitrary[String]
  } yield SlaveID(id)

  implicit lazy val arbSlaveID: Arbitrary[SlaveID] = Arbitrary(slaveIDGen)

  val executorIDGen = for {
    id <- arbitrary[String]
  } yield ExecutorID(id)

  implicit lazy val arbExecutorID: Arbitrary[ExecutorID] = Arbitrary(executorIDGen)

  val frameworkIDGen = for {
    id <- arbitrary[String]
  } yield FrameworkID(id)

  implicit lazy val arbFrameworkID: Arbitrary[FrameworkID] = Arbitrary(frameworkIDGen)

  val taskStateGen = {
    import TaskState._

    Gen.oneOf(TaskError, TaskFailed, TaskFinished, TaskKilled, TaskLost, TaskRunning, TaskStaging, TaskStarting)
  }

  implicit lazy val arbTaskState: Arbitrary[TaskState] = Arbitrary(taskStateGen)

  val byteStringGen = for {
    bytes <- arbitrary[Array[Byte]]
  } yield ByteString(bytes)

  implicit lazy val arbByteString: Arbitrary[ByteString] = Arbitrary(byteStringGen)
}
