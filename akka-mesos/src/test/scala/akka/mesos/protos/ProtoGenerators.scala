package akka.mesos.protos

import akka.mesos.protos.DockerContainerInfo.{ PortMapping, Network }
import akka.util.ByteString
import org.scalacheck.{ Gen, Arbitrary }

import scala.collection.immutable.{ Seq, NumericRange }

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

  val longRangeGen: Gen[NumericRange[Long]] = for {
    begin <- Gen.posNum[Long]
    end <- Gen.chooseNum[Long](begin + 1, begin + Int.MaxValue - 1)
  } yield begin.toLong to end

  implicit lazy val arbLongRange = Arbitrary(longRangeGen)

  val longRangeSeqGen: Gen[Seq[NumericRange[Long]]] = Gen.sized { x =>
    Gen.listOfN(x % Int.MaxValue, longRangeGen)
  }

  implicit lazy val arbLongRangeSeq = Arbitrary(longRangeSeqGen)

  val resourceTypeGen = {
    import ResourceType._

    Gen.oneOf(CPUS, DISK, MEM, PORTS)
  }

  implicit lazy val arbResourceType: Arbitrary[ResourceType] = Arbitrary(resourceTypeGen)

  val scalarResourceGen = for {
    tpe <- arbitrary[ResourceType]
    value <- arbitrary[Double]
    role <- arbitrary[Option[String]]
  } yield ScalarResource(tpe, value, role)

  lazy val arbScalarResource = Arbitrary(scalarResourceGen)

  val rangesResourceGen = for {
    tpe <- arbitrary[ResourceType]
    value <- arbitrary[Seq[NumericRange[Long]]]
    role <- arbitrary[Option[String]]
  } yield RangesResource(tpe, value, role)

  implicit lazy val arbRangesResource: Arbitrary[RangesResource] = Arbitrary(rangesResourceGen)

  val setResourceGen = for {
    tpe <- arbitrary[ResourceType]
    value <- arbitrary[Seq[String]]
    role <- arbitrary[Option[String]]
  } yield SetResource(tpe, value, role)

  implicit lazy val arbSetResource: Arbitrary[SetResource] = Arbitrary(setResourceGen)

  val resourceGen = Gen.oneOf(scalarResourceGen, rangesResourceGen, setResourceGen)

  implicit lazy val arbResource = Arbitrary(resourceGen)

  val variableGen = for {
    name <- arbitrary[String]
    value <- arbitrary[String]
  } yield Environment.Variable(name, value)

  implicit lazy val arbVariable = Arbitrary(variableGen)

  val environmentGen = for {
    variables <- arbitrary[Seq[Environment.Variable]]
  } yield Environment(variables)

  implicit lazy val arbEnvironment = Arbitrary(environmentGen)

  val commandInfoContainerInfoGen = for {
    image <- arbitrary[String]
    options <- arbitrary[Seq[String]]
  } yield CommandInfo.ContainerInfo(image, options)

  implicit lazy val arbCommandInfoContainerInfo: Arbitrary[CommandInfo.ContainerInfo] =
    Arbitrary(commandInfoContainerInfoGen)

  val commandInfoURIGen = for {
    uri <- arbitrary[String]
    extract <- arbitrary[Option[Boolean]]
    executable <- arbitrary[Option[Boolean]]
  } yield CommandInfo.URI(uri, extract, executable)

  implicit lazy val arbCommandInfoURI: Arbitrary[CommandInfo.URI] = Arbitrary(commandInfoURIGen)

  val volumeModeGen = Gen.oneOf(Volume.Mode.RO, Volume.Mode.RW)
  implicit lazy val arbVolumeMode: Arbitrary[Volume.Mode] = Arbitrary(volumeModeGen)

  val volumeGen = for {
    containerPath <- arbitrary[String]
    mode <- arbitrary[Volume.Mode]
    hostPath <- arbitrary[Option[String]]
  } yield Volume(containerPath, mode, hostPath)

  implicit lazy val arbVolume = Arbitrary(volumeGen)

  val networkGen = {
    import Network._
    Gen.oneOf(Bridge, Host, None)
  }

  implicit lazy val arbNetwork: Arbitrary[Network] = Arbitrary(networkGen)

  val portMappingGen = for {
    hostPort <- arbitrary[Int]
    containerPort <- arbitrary[Int]
    protocol <- arbitrary[Option[String]]
  } yield PortMapping(hostPort, containerPort, protocol)

  implicit lazy val arbPortMapping: Arbitrary[PortMapping] = Arbitrary(portMappingGen)

  val parameterGen = for {
    key <- arbitrary[String]
    value <- arbitrary[String]
  } yield Parameter(key, value)

  implicit lazy val arbParameter: Arbitrary[Parameter] = Arbitrary(parameterGen)
}
