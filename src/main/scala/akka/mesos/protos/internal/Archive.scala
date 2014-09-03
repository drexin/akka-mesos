package akka.mesos.protos.internal

import akka.mesos.protos.FrameworkInfo
import mesos.internal.Messages

import scala.collection.JavaConverters._

final case class Archive {

}

object Archive {
  case class Framework(
      frameworkInfo: FrameworkInfo,
      tasks: Seq[Task],
      pid: Option[String]) {
    def toProto: Messages.Archive.Framework = {
      val builder = Messages.Archive.Framework
        .newBuilder
        .setFrameworkInfo(frameworkInfo.toProto)
        .addAllTasks(tasks.map(_.toProto).asJava)

      pid.foreach(builder.setPid)

      builder.build()
    }
  }
}
