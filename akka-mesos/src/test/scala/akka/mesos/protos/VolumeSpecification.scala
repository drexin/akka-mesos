package akka.mesos.protos

import org.apache.mesos.Protos
import org.scalacheck.Prop._
import org.scalacheck.{ Arbitrary, Gen, Properties }

import scala.util.Success

object VolumeSpecification extends Properties("Volume") {
  import org.scalacheck.Prop.forAll

  val volumeModeGen = Gen.oneOf(Volume.Mode.RO, Volume.Mode.RW)
  implicit lazy val arbVolumeMode: Arbitrary[Volume.Mode] = Arbitrary(volumeModeGen)

  property("toProto") = forAll { (containerPath: String, mode: Volume.Mode, hostPath: Option[String]) =>
    val builder = Protos.Volume
      .newBuilder
      .setContainerPath(containerPath)
      .setMode(mode.toProto)

    hostPath.foreach(builder.setHostPath)

    val proto = builder.build()

    Volume(containerPath, mode, hostPath).toProto == proto
  }

  property("apply") = forAll { (containerPath: String, mode: Volume.Mode, hostPath: Option[String]) =>
    val builder = Protos.Volume
      .newBuilder
      .setContainerPath(containerPath)
      .setMode(mode.toProto)

    hostPath.foreach(builder.setHostPath)

    val proto = builder.build()

    Volume(proto) == Volume(containerPath, mode, hostPath)
  }

  property("fromBytes") = forAll { (containerPath: String, mode: Volume.Mode, hostPath: Option[String]) =>
    val builder = Protos.Volume
      .newBuilder
      .setContainerPath(containerPath)
      .setMode(mode.toProto)

    hostPath.foreach(builder.setHostPath)

    val proto = builder.build()

    Volume.fromBytes(proto.toByteArray) == Success(Volume(containerPath, mode, hostPath))
  }
}
