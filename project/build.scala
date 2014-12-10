import sbt._
import Keys._
import sbtprotobuf.{ ProtobufPlugin => PB }
import sbtrelease.ReleasePlugin._
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

object AkkaMesosBuild extends Build {

  lazy val root = Project(
    id = "akka-mesos-root",
    base = file("."),
    aggregate = Seq(libprocess, mesos),
    settings = baseSettings
  )

  lazy val mesos = Project(
    id = "akka-mesos",
    base = file("akka-mesos"),
    settings = baseSettings ++ PB.protobufSettings ++ Seq(
      libraryDependencies ++= Dependencies.mesos,
      javaSource in PB.protobufConfig <<= (sourceDirectory in Compile)(_ / "java")
    ),
    dependencies = Seq(libprocess)
  )

  lazy val libprocess = Project(
    id = "akka-libprocess",
    base = file("akka-libprocess"),
    settings = baseSettings ++ Seq(
      libraryDependencies ++= Dependencies.libprocess
    )
  )


  lazy val baseSettings = Defaults.defaultSettings ++ formatSettings ++ releaseSettings ++ Seq(
    organization := "io.mesosphere.akka",
    scalaVersion := "2.11.2",

    scalacOptions in Compile ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-Xlint",
      "-Xfatal-warnings",
      "-Yno-adapted-args",
      "-Ywarn-unused-import"
    ),
    parallelExecution in Test := false
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
      .setPreference(CompactControlReadability, false)
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

  val mesos = Seq(
    akkaActor   % "compile",
    protobuf    % "compile"
  )

  val libprocess = Seq(
    config            % "compile",
    slf4j             % "compile",
    akkaActor         % "compile",
    logback           % "compile",
    akkaHttp          % "compile",
    Test.akkaTestKit  % "test",
    Test.scalaTest    % "test"
  )
}

object Dependency {
  object V {
    val Akka        = "2.3.6"
    val AkkaHttp    = "1.0-M1"
    val Protobuf    = "2.5.0"
    val Config      = "1.2.1"
    val ScalaTest   = "2.1.3"
    val Slf4j       = "1.7.2"
    val Logback     = "1.0.9"
  }

  val akkaActor   = "com.typesafe.akka"   %%  "akka-actor"                  % V.Akka
  val protobuf    = "com.google.protobuf" %   "protobuf-java"               % V.Protobuf
  val config      = "com.typesafe"        %   "config"                      % V.Config
  val slf4j       = "org.slf4j"           %   "slf4j-api"                   % V.Slf4j
  val logback     = "ch.qos.logback"      %   "logback-classic"             % V.Logback
  val akkaHttp    = "com.typesafe.akka"   %%  "akka-http-core-experimental" % V.AkkaHttp

  object Test {
    val akkaTestKit = "com.typesafe.akka" %% "akka-testkit"    % V.Akka
    val scalaTest   = "org.scalatest"     %% "scalatest"       % V.ScalaTest
  }
}

