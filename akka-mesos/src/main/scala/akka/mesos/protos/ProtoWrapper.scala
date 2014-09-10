package akka.mesos.protos

import com.google.protobuf.MessageLite

trait ProtoWrapper[A <: MessageLite] {
  def toProto: A
}
