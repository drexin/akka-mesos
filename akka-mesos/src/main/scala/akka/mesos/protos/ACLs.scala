package akka.mesos.protos

import akka.mesos.protos.ACL.{ ShutdownFramework, RunTask, RegisterFramework }
import org.apache.mesos.Protos

import scala.collection.JavaConverters._

final case class ACLs(
    permissive: Option[Boolean] = None,
    registerFrameworks: Seq[RegisterFramework] = Nil,
    runTasks: Seq[RunTask] = Nil,
    shutdownFrameworks: Seq[ShutdownFramework] = Nil) {
  def toProto: Protos.ACLs = {
    val builder = Protos.ACLs
      .newBuilder
      .addAllRegisterFrameworks(registerFrameworks.map(_.toProto).asJava)
      .addAllRunTasks(runTasks.map(_.toProto).asJava)
      .addAllShutdownFrameworks(shutdownFrameworks.map(_.toProto).asJava)

    permissive.foreach(builder.setPermissive)

    builder.build()
  }
}
