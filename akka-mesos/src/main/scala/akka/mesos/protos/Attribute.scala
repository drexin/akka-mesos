package akka.mesos.protos

import org.apache.mesos.Protos

sealed trait Attribute extends ProtoWrapper[Protos.Attribute] {
  def toProto: Protos.Attribute
}

object Attribute {
  def apply(proto: Protos.Attribute) = proto.getType match {
    case Protos.Value.Type.TEXT =>
      TextAttribute(proto.getName, proto.getText.getValue)

    case _ => ???
  }
}

final case class TextAttribute(name: String, text: String) extends Attribute {
  def toProto: Protos.Attribute =
    Protos.Attribute
      .newBuilder
      .setName(name)
      .setType(Protos.Value.Type.TEXT)
      .setText(
        Protos.Value.Text
          .newBuilder
          .setValue(text)
          .build())
      .build()
}
