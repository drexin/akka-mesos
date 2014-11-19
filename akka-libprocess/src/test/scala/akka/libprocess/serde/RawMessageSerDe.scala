package akka.libprocess.serde

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream }

import akka.util.ByteString

import scala.util.Try

class RawMessageSerDe extends MessageSerDe {
  override def deserialize(message: TransportMessage): Try[AnyRef] = Try {
    val bs = new ByteArrayInputStream(message.data.toArray)
    val is = new ObjectInputStream(bs)

    is.readObject()
  }

  override def serialize(obj: AnyRef): Try[TransportMessage] = Try {
    val bs = new ByteArrayOutputStream()
    val os = new ObjectOutputStream(bs)
    os.writeObject(obj)
    val res = ByteString(bs.toByteArray)
    os.close()
    TransportMessage(obj.getClass.getCanonicalName, res)
  }
}
