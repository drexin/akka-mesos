package akka.mesos.protos

import akka.util.ByteString
import org.apache.mesos.Protos
import com.google.protobuf.{ ByteString => PBByteString }

final case class Credential(principal: String, secret: Option[ByteString] = None) {
  def toProto: Protos.Credential = {
    val builder = Protos.Credential
      .newBuilder
      .setPrincipal(principal)

    secret.foreach(x => builder.setSecret(PBByteString.copyFrom(x.toArray)))

    builder.build()
  }
}
