package akka.mesos.protos

import org.apache.mesos.Protos.{ Environment => PBEnvironment }
import PBEnvironment.{ Variable => PBVariable }
import scala.collection.JavaConverters._

case class Environment(variables: Seq[Environment.Variable]) {
  def toProtos: PBEnvironment =
    PBEnvironment
      .newBuilder
      .addAllVariables(variables.map(_.toProtos).asJava)
      .build()
}

object Environment {
  case class Variable(name: String, value: String) {
    def toProtos: PBVariable =
      PBVariable
        .newBuilder
        .setName(name)
        .setValue(value)
        .build()
  }
}

