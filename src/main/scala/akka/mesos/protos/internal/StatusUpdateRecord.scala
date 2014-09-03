package akka.mesos.protos.internal

import akka.util.ByteString
import com.google.protobuf.{ ByteString => PBByteString }
import mesos.internal.Messages

final case class StatusUpdateRecord(
    tpe: StatusUpdateRecord.Type,
    update: Option[StatusUpdate],
    uuid: Option[ByteString]) {
  def toProto: Messages.StatusUpdateRecord = {
    val builder = Messages.StatusUpdateRecord
      .newBuilder
      .setType(tpe.toProto)

    update.foreach(x => builder.setUpdate(x.toProto))
    uuid.foreach(x => builder.setUuid(PBByteString.copyFrom(x.toArray)))

    builder.build()
  }
}

object StatusUpdateRecord {
  sealed trait Type {
    def toProto: Messages.StatusUpdateRecord.Type
  }

  case object Ack extends Type {
    def toProto: Messages.StatusUpdateRecord.Type = Messages.StatusUpdateRecord.Type.ACK
  }

  case object Update extends Type {
    def toProto: Messages.StatusUpdateRecord.Type = Messages.StatusUpdateRecord.Type.UPDATE
  }
}
