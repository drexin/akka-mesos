import sbt._
import Keys._
import sbtprotobuf.{ ProtobufPlugin => PB }

object AkkaMesosBuild extends Build {
  lazy val core = Project(
    id = "core",
    base = file("."),
    settings = baseSettings ++ PB.protobufSettings ++ Seq(
      libraryDependencies ++= Dependencies.core,
      javaSource in PB.protobufConfig <<= (sourceDirectory in Compile)(_ / "java")
    )
  ) dependsOn akkaLibprocess

  lazy val akkaLibprocess = RootProject(uri("git://github.com/drexin/akka-libprocess.git"))

  lazy val baseSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1.0",
    organization := "akka.libprocess",
    scalaVersion := "2.10.4",

    scalacOptions in Compile ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    )
  )
}

object Dependencies {
  import Dependency._

  val core = Seq(
    akkaActor   % "compile",
    protobuf    % "compile"
  )
}

object Dependency {
  object V {
    val Akka      = "2.3.3"
    val Protobuf  = "2.5.0"
  }

  val akkaActor   = "com.typesafe.akka"   %%  "akka-actor"      % V.Akka
  val protobuf    = "com.google.protobuf" %   "protobuf-java"   % V.Protobuf
}

