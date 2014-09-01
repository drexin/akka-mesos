package akka.mesos.protos

import org.apache.mesos.Protos

import scala.collection.JavaConverters._

object ACL {
  final case class Entity(
      tpe: Option[Entity.Type] = None,
      values: Seq[String] = Nil) {
    def toProto: Protos.ACL.Entity = {
      val builder = Protos.ACL.Entity
        .newBuilder
        .addAllValues(values.asJava)

      tpe.foreach(x => builder.setType(x.toProto))

      builder.build()
    }
  }

  object Entity {
    sealed trait Type {
      def toProto: Protos.ACL.Entity.Type
    }

    case object Some extends Type {
      def toProto: Protos.ACL.Entity.Type = Protos.ACL.Entity.Type.SOME
    }

    case object Nome extends Type {
      def toProto: Protos.ACL.Entity.Type = Protos.ACL.Entity.Type.NONE
    }

    case object Any extends Type {
      def toProto: Protos.ACL.Entity.Type = Protos.ACL.Entity.Type.ANY
    }
  }

  final case class RegisterFramework(
      principals: Entity,
      roles: Entity) {
    def toProto: Protos.ACL.RegisterFramework =
      Protos.ACL.RegisterFramework
        .newBuilder
        .setPrincipals(principals.toProto)
        .setRoles(roles.toProto)
        .build()
  }

  final case class RunTask(
      principals: Entity,
      users: Entity) {
    def toProto: Protos.ACL.RunTask =
      Protos.ACL.RunTask
        .newBuilder
        .setPrincipals(principals.toProto)
        .setUsers(users.toProto)
        .build()
  }

  final case class ShutdownFramework(
      principals: Entity,
      frameworkPrincipals: Entity) {
    def toProto: Protos.ACL.ShutdownFramework =
      Protos.ACL.ShutdownFramework
        .newBuilder
        .setPrincipals(principals.toProto)
        .setFrameworkPrincipals(frameworkPrincipals.toProto)
        .build()
  }
}
