import sbt._
import Keys._
import sbtprotobuf.{ ProtobufPlugin => PB }
import sbtrelease.ReleasePlugin._
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

object AkkaMesosBuild extends Build {
  lazy val core = Project(
    id = "core",
    base = file("."),
    settings = baseSettings ++ PB.protobufSettings ++ Seq(
      libraryDependencies ++= Dependencies.core,
      javaSource in PB.protobufConfig <<= (sourceDirectory in Compile)(_ / "java")
    )
  )

  lazy val baseSettings = Defaults.defaultSettings ++ formatSettings ++ releaseSettings ++ Seq(
    version := "0.1.0",
    organization := "akka.libprocess",
    scalaVersion := "2.10.4",

    scalacOptions in Compile ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    )
  )

  lazy val formatSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := FormattingPreferences()
      .setPreference(IndentWithTabs, false)
      .setPreference(IndentSpaces, 2)
      .setPreference(AlignParameters, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(CompactControlReadability, true) //MV: should be false!
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpaceInsideParentheses, false)
      .setPreference(SpacesWithinPatternBinders, true)
      .setPreference(FormatXml, true)
  )
}

object Dependencies {
  import Dependency._

  val core = Seq(
    akkaActor   % "compile",
    protobuf    % "compile",
    libprocess  % "compile"
  )
}

object Dependency {
  object V {
    val Akka        = "2.3.3"
    val Protobuf    = "2.5.0"
    val Libprocess  = "0.1.0-SNAPSHOT"
  }

  val akkaActor   = "com.typesafe.akka"   %%  "akka-actor"      % V.Akka
  val protobuf    = "com.google.protobuf" %   "protobuf-java"   % V.Protobuf
  val libprocess  = "io.mesosphere"       %%  "akka-libprocess" % V.Libprocess
}

