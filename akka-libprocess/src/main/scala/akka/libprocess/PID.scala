/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess

import scala.util.{ Failure, Success, Try }

class InvalidPIDException(pid: String) extends Exception(s"Invalid PID: $pid")

case class PID(ip: String, port: Int, id: String) {
  def toAddressString = s"$id@$ip:$port"
}

object PID {
  val addressStringRegex = """^([^@]+)@(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}):(\d+)$""".r

  def fromAddressString(pidStr: String): Try[PID] = pidStr match {
    case addressStringRegex(id, ip, port) => Success(PID(ip, port.toInt, id))
    case _                                => Failure(new InvalidPIDException(pidStr))
  }
}
