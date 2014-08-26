package akka.mesos.protos

import org.apache.mesos.Protos.{ Attribute => PBAttribute, Value }

sealed trait Attribute {
  def toProto: PBAttribute
}

case class TextAttribute(name: String, text: String) extends Attribute {
  def toProto: PBAttribute =
    PBAttribute
      .newBuilder
      .setName(name)
      .setType(Value.Type.TEXT)
      .setText(
        Value.Text
          .newBuilder
          .setValue(text)
          .build())
      .build()
}
