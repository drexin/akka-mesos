package akka.mesos.protos

import java.util.concurrent.TimeUnit

import org.apache.mesos.Protos

import scala.concurrent.duration._
import scala.collection.immutable.Seq
import scala.collection.JavaConverters._
import scala.util.Try

sealed trait HealthCheck extends ProtoWrapper[Protos.HealthCheck] {
  def delay: Option[Duration]
  def interval: Option[Duration]
  def timeout: Option[Duration]
  def gracePeriod: Option[Duration]
  def consecutiveFailures: Option[Int]

  def toProto: Protos.HealthCheck = {
    val builder = Protos.HealthCheck.newBuilder

    delay.foreach(d => builder.setDelaySeconds(d.toMillis.toDouble / 1000))
    interval.foreach(d => builder.setIntervalSeconds(d.toMillis.toDouble / 1000))
    timeout.foreach(d => builder.setTimeoutSeconds(d.toMillis.toDouble / 1000))
    gracePeriod.foreach(d => builder.setGracePeriodSeconds(d.toMillis.toDouble / 1000))
    consecutiveFailures.foreach(builder.setConsecutiveFailures)

    builder.build()
  }
}

object HealthCheck extends ProtoReads[HealthCheck] {
  def apply(proto: Protos.HealthCheck): HealthCheck = {
    if (proto.hasHttp) HTTPHealthCheck(proto)
    else CommandHealthCheck(proto)
  }

  override def fromBytes(bytes: Array[Byte]): Try[HealthCheck] = Try {
    HealthCheck(Protos.HealthCheck.parseFrom(bytes))
  }
}

final case class HTTPHealthCheck(
    port: Int,
    statuses: Seq[Int],
    path: Option[String] = None,
    delay: Option[Duration] = None,
    interval: Option[Duration] = None,
    timeout: Option[Duration] = None,
    gracePeriod: Option[Duration] = None,
    consecutiveFailures: Option[Int] = None) extends HealthCheck {
  override def toProto: Protos.HealthCheck = {
    val httpBuilder = Protos.HealthCheck.HTTP
      .newBuilder
      .setPort(port)
      .addAllStatuses(statuses.map(Int.box).asJava)

    path.foreach(httpBuilder.setPath)

    Protos.HealthCheck
      .newBuilder
      .setHttp(httpBuilder)
      .mergeFrom(super.toProto)
      .build()
  }
}

object HTTPHealthCheck {
  def apply(proto: Protos.HealthCheck): HTTPHealthCheck = {
    val http = proto.getHttp
    val path = if (http.hasPath) Some(http.getPath) else None
    val delay = if (proto.hasDelaySeconds) Some(Duration(proto.getDelaySeconds, TimeUnit.SECONDS)) else None
    val interval = if (proto.hasIntervalSeconds) Some(Duration(proto.getIntervalSeconds, TimeUnit.SECONDS)) else None
    val timeout = if (proto.hasTimeoutSeconds) Some(Duration(proto.getTimeoutSeconds, TimeUnit.SECONDS)) else None
    val gracePeriod = if (proto.hasGracePeriodSeconds) Some(Duration(proto.getGracePeriodSeconds, TimeUnit.SECONDS)) else None
    val consecutiveFailures = if (proto.hasConsecutiveFailures) Some(proto.getConsecutiveFailures) else None

    HTTPHealthCheck(
      http.getPort,
      http.getStatusesList.asScala.to[Seq].map(_.intValue),
      path,
      delay,
      interval,
      timeout,
      gracePeriod,
      consecutiveFailures)
  }
}

final case class CommandHealthCheck(
    command: CommandInfo,
    delay: Option[Duration] = None,
    interval: Option[Duration] = None,
    timeout: Option[Duration] = None,
    gracePeriod: Option[Duration] = None,
    consecutiveFailures: Option[Int] = None) extends HealthCheck {
  override def toProto: Protos.HealthCheck = {
    Protos.HealthCheck
      .newBuilder
      .setCommand(command.toProto)
      .mergeFrom(super.toProto)
      .build()
  }
}

object CommandHealthCheck {
  def apply(proto: Protos.HealthCheck): CommandHealthCheck = {
    val delay = if (proto.hasDelaySeconds) Some(Duration(proto.getDelaySeconds, TimeUnit.SECONDS)) else None
    val interval = if (proto.hasIntervalSeconds) Some(Duration(proto.getIntervalSeconds, TimeUnit.SECONDS)) else None
    val timeout = if (proto.hasTimeoutSeconds) Some(Duration(proto.getTimeoutSeconds, TimeUnit.SECONDS)) else None
    val gracePeriod = if (proto.hasGracePeriodSeconds) Some(Duration(proto.getGracePeriodSeconds, TimeUnit.SECONDS)) else None
    val consecutiveFailures = if (proto.hasConsecutiveFailures) Some(proto.getConsecutiveFailures) else None

    CommandHealthCheck(
      CommandInfo(proto.getCommand),
      delay,
      interval,
      timeout,
      gracePeriod,
      consecutiveFailures)
  }
}
