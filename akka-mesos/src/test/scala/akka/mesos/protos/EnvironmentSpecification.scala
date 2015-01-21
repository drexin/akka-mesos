package akka.mesos.protos

import akka.mesos.protos.Environment.Variable
import org.apache.mesos.Protos
import org.scalacheck.{ Arbitrary, Properties }

import scala.collection.JavaConverters._
import scala.util.Success

object EnvironmentSpecification extends Properties("Environment") with ProtoGenerators {
  import org.scalacheck.Prop.forAll

  property("toProto") = forAll { (variables: Seq[Variable]) =>
    val proto = Protos.Environment
      .newBuilder
      .addAllVariables(variables.map(_.toProto).asJava)
      .build()

    Environment(variables).toProto == proto
  }

  property("apply") = forAll { (variables: Seq[Variable]) =>
    val proto = Protos.Environment
      .newBuilder
      .addAllVariables(variables.map(_.toProto).asJava)
      .build()

    Environment(proto) == Environment(variables)
  }

  property("fromBytes") = forAll { (variables: Seq[Variable]) =>
    val proto = Protos.Environment
      .newBuilder
      .addAllVariables(variables.map(_.toProto).asJava)
      .build()

    Environment.fromBytes(proto.toByteArray) == Success(Environment(variables))
  }
}

object VariableSpecification extends Properties("Environment.Variable") {
  import org.scalacheck.Prop.forAll
  import Environment.Variable

  property("toProto") = forAll { (name: String, value: String) =>
    val proto = Protos.Environment.Variable
      .newBuilder
      .setName(name)
      .setValue(value)
      .build()

    Variable(name, value).toProto == proto
  }

  property("apply") = forAll { (name: String, value: String) =>
    val proto = Protos.Environment.Variable
      .newBuilder
      .setName(name)
      .setValue(value)
      .build()

    Variable(proto) == Variable(name, value)
  }

  property("fromBytes") = forAll { (name: String, value: String) =>
    val proto = Protos.Environment.Variable
      .newBuilder
      .setName(name)
      .setValue(value)
      .build()

    Variable.fromBytes(proto.toByteArray) == Success(Variable(name, value))
  }
}
