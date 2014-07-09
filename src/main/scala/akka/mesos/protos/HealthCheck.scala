package akka.mesos.protos

import scala.concurrent.duration._
import org.apache.mesos.Protos.{ HealthCheck => PBHealthCheck }
import scala.collection.JavaConverters._

trait HealthCheck {
  def delay: Option[Duration]
  def interval: Option[Duration]
  def timeout: Option[Duration]
  def gracePeriod: Option[Duration]
  def failures: Option[Int]
  def command: Option[CommandInfo]

  def toProtos: PBHealthCheck
}

case class HTTPHealthCheck(
  port: Int,
  statuses: Seq[Int],
  path: Option[String] = None,
  delay: Option[Duration] = None,
  interval: Option[Duration] = None,
  timeout: Option[Duration] = None,
  gracePeriod: Option[Duration] = None,
  failures: Option[Int] = None,
  command: Option[CommandInfo] = None
) extends HealthCheck {
  def toProtos: PBHealthCheck = {
    val httpBuilder = PBHealthCheck.HTTP
      .newBuilder
      .setPort(port)
      .addAllStatuses(statuses.map(Int.box).asJava)

    path.foreach(httpBuilder.setPath)

    val builder = PBHealthCheck.newBuilder.setHttp(httpBuilder.build)

    delay.foreach(d => builder.setDelaySeconds(d.toMillis.toDouble / 1000))
    interval.foreach(d => builder.setIntervalSeconds(d.toMillis.toDouble / 1000))
    timeout.foreach(d => builder.setTimeoutSeconds(d.toMillis.toDouble / 1000))
    gracePeriod.foreach(d => builder.setGracePeriodSeconds(d.toMillis.toDouble / 1000))
    failures.foreach(builder.setFailures)
    command.foreach(c => builder.setCommand(c.toProtos))

    builder.build()
  }
}

