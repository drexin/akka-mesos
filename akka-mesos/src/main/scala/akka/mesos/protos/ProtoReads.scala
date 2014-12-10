package akka.mesos.protos

import scala.util.Try

trait ProtoReads[A <: ProtoWrapper[_]] extends (Array[Byte] => Try[A]) {
  def apply(bytes: Array[Byte]): Try[A] = fromBytes(bytes)
  def fromBytes(bytes: Array[Byte]): Try[A]
}
