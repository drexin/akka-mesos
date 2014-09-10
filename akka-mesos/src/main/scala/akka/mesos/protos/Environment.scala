package akka.mesos.protos

import org.apache.mesos.Protos
import scala.collection.JavaConverters._

final case class Environment(variables: Seq[Environment.Variable]) extends ProtoWrapper[Protos.Environment] {
  def toProto: Protos.Environment =
    Protos.Environment
      .newBuilder
      .addAllVariables(variables.map(_.toProto).asJava)
      .build()
}

object Environment {

  def apply(proto: Protos.Environment): Environment =
    Environment(proto.getVariablesList.asScala.map(Variable(_)))

  final case class Variable(
      name: String,
      value: String) {
    def toProto: Protos.Environment.Variable =
      Protos.Environment.Variable
        .newBuilder
        .setName(name)
        .setValue(value)
        .build()
  }

  object Variable {
    def apply(proto: Protos.Environment.Variable): Variable =
      Variable(proto.getName, proto.getValue)
  }
}
