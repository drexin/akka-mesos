package akka.mesos.protos.internal

import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

final case class AuthenticationStepMessage(data: ByteString) {
  def toProto: Messages.AuthenticationStepMessage =
    Messages.AuthenticationStepMessage
      .newBuilder
      .setData(PBByteString.copyFrom(data.toArray))
      .build()
}
