package akka.mesos.protos

import akka.mesos.protos.DockerContainerInfo.{ PortMapping, Network }
import akka.mesos.protos.Environment.Variable
import akka.util.ByteString
import org.scalacheck.{ Gen, Arbitrary }

import scala.collection.immutable._
import scala.concurrent.duration._

trait ProtoGenerators {
  import Arbitrary.arbitrary

  val durationGen = Gen.chooseNum(Long.MinValue + 1, Long.MaxValue).map(_.nanos)

  implicit lazy val arbDuration: Arbitrary[Duration] = Arbitrary(durationGen)

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

  implicit lazy val arbLongRange: Arbitrary[NumericRange[Long]] = Arbitrary(longRangeGen)

  val longRangeSeqGen: Gen[Seq[NumericRange[Long]]] = Gen.sized { x =>
    Gen.listOfN(x % Int.MaxValue, longRangeGen)
  }

  implicit lazy val arbLongRangeSeq: Arbitrary[Seq[NumericRange[Long]]] = Arbitrary(longRangeSeqGen)

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

  lazy val arbScalarResource: Arbitrary[ScalarResource] = Arbitrary(scalarResourceGen)

  val rangesResourceGen = for {
    tpe <- arbitrary[ResourceType]
    value <- arbitrary[Seq[NumericRange[Long]]]
    role <- arbitrary[Option[String]]
  } yield RangesResource(tpe, value, role)

  implicit lazy val arbRangesResource: Arbitrary[RangesResource] = Arbitrary(rangesResourceGen)

  val setResourceGen = for {
    tpe <- arbitrary[ResourceType]
    value <- arbitrary[Set[String]]
    role <- arbitrary[Option[String]]
  } yield SetResource(tpe, value, role)

  implicit lazy val arbSetResource: Arbitrary[SetResource] = Arbitrary(setResourceGen)

  val resourceGen = Gen.oneOf(scalarResourceGen, rangesResourceGen, setResourceGen)

  implicit lazy val arbResource: Arbitrary[Resource] = Arbitrary(resourceGen)

  val variableGen = for {
    name <- arbitrary[String]
    value <- arbitrary[String]
  } yield Environment.Variable(name, value)

  implicit lazy val arbVariable: Arbitrary[Variable] = Arbitrary(variableGen)

  val environmentGen = for {
    variables <- arbitrary[Seq[Environment.Variable]]
  } yield Environment(variables)

  implicit lazy val arbEnvironment: Arbitrary[Environment] = Arbitrary(environmentGen)

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

  implicit lazy val arbVolume: Arbitrary[Volume] = Arbitrary(volumeGen)

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

  val commandInfoGen = for {
    value <- arbitrary[String]
    uris <- arbitrary[Seq[CommandInfo.URI]]
    environment <- arbitrary[Option[Environment]]
    container <- arbitrary[Option[CommandInfo.ContainerInfo]]
    user <- arbitrary[Option[String]]
    shell <- arbitrary[Option[Boolean]]
    arguments <- arbitrary[Seq[String]]
  } yield CommandInfo(value, uris, environment, container, user, shell, arguments)

  implicit lazy val arbCommandInfo: Arbitrary[CommandInfo] = Arbitrary(commandInfoGen)

  val dockerContainerInfoGen = for {
    image <- arbitrary[String]
    hostname <- arbitrary[Option[String]]
    volumes <- arbitrary[Seq[Volume]]
    network <- arbitrary[Option[Network]]
    portMappings <- arbitrary[Seq[PortMapping]]
    privileged <- arbitrary[Option[Boolean]]
    parameters <- arbitrary[Seq[Parameter]]
    forcePullImage <- arbitrary[Option[Boolean]]
  } yield DockerContainerInfo(image, hostname, volumes, network, portMappings, privileged, parameters, forcePullImage)

  implicit lazy val arbDockerContainerInfo: Arbitrary[DockerContainerInfo] = Arbitrary(dockerContainerInfoGen)

  val containerInfoGen = dockerContainerInfoGen

  implicit lazy val arbContainerInfo: Arbitrary[ContainerInfo] = Arbitrary(containerInfoGen)

  val httpHealthCheckGen = for {
    port <- arbitrary[Int]
    statuses <- arbitrary[Seq[Int]]
    path <- arbitrary[Option[String]]
    delay <- arbitrary[Option[Duration]]
    interval <- arbitrary[Option[Duration]]
    timeout <- arbitrary[Option[Duration]]
    gracePeriod <- arbitrary[Option[Duration]]
    consecutiveFailures <- arbitrary[Option[Int]]
  } yield HTTPHealthCheck(port, statuses, path, delay, interval, timeout, gracePeriod, consecutiveFailures)

  implicit lazy val arbHTTPHealthCheck: Arbitrary[HTTPHealthCheck] = Arbitrary(httpHealthCheckGen)

  val commandHealthCheckGen = for {
    command <- arbitrary[CommandInfo]
    delay <- arbitrary[Option[Duration]]
    interval <- arbitrary[Option[Duration]]
    timeout <- arbitrary[Option[Duration]]
    gracePeriod <- arbitrary[Option[Duration]]
    consecutiveFailures <- arbitrary[Option[Int]]
  } yield CommandHealthCheck(command, delay, interval, timeout, gracePeriod, consecutiveFailures)

  implicit lazy val arbCommandHealthCheck: Arbitrary[CommandHealthCheck] = Arbitrary(commandHealthCheckGen)

  val healthCheckGen = Gen.oneOf(httpHealthCheckGen, commandHealthCheckGen)

  implicit lazy val arbHealthCheck: Arbitrary[HealthCheck] = Arbitrary(healthCheckGen)

  val executorInfoGen = for {
    executorID <- arbitrary[ExecutorID]
    command <- arbitrary[CommandInfo]
    resources <- arbitrary[Seq[Resource]]
    frameworkId <- arbitrary[Option[FrameworkID]]
    name <- arbitrary[Option[String]]
    source <- arbitrary[Option[String]]
    data <- arbitrary[Option[ByteString]]
    container <- arbitrary[Option[ContainerInfo]]
  } yield ExecutorInfo(executorID, command, resources, frameworkId, name, source, data, container)

  implicit lazy val arbExecutoInfo: Arbitrary[ExecutorInfo] = Arbitrary(executorInfoGen)
}
