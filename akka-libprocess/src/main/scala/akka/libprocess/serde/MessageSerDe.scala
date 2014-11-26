/*
 * Copyright (c) 2014 Mesosphere Inc. <http://www.mesosphere.io>
 */

package akka.libprocess.serde

import akka.util.ByteString

import scala.util.Try

trait MessageSerDe {
  def deserialize(message: TransportMessage): Try[AnyRef]
  def serialize(obj: AnyRef): Try[TransportMessage]
}

case class TransportMessage(messageName: String, data: ByteString)
