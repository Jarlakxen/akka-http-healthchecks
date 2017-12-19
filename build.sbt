import sbt.Keys._

// ··· Project Info ···

name := "akka-http-healthchecks"

organization := "com.github.jarlakxen"

crossScalaVersions := Seq("2.12.4")

scalaVersion := { crossScalaVersions.value.head }

fork in run  := true

publishMavenStyle := true

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// ··· Project Enviroment ···


// ··· Project Options ···

scalacOptions ++= Seq(
    "-encoding",
    "utf8",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-unchecked",
    "-deprecation"
)

scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution := false

// ··· Project Repositories ···

resolvers ++= Seq(Resolver.jcenterRepo)

// ··· Project Dependancies ···

val akkaV             = "2.5.8"
val akkaHttpV         = "10.0.11"
val slf4JV            = "1.7.25"
val logbackV          = "1.2.3"
val scalatestV        = "3.0.4"
val scalacticV        = "3.0.4"

libraryDependencies ++= Seq(
  // --- Akka --
  "com.typesafe.akka"             %% "akka-actor"                         % akkaV             %  "provided",
  "com.typesafe.akka"             %% "akka-slf4j"                         % akkaV             %  "provided",
  "com.typesafe.akka"             %% "akka-http"                          % akkaHttpV         %  "provided",
  // --- Logger ---
  "org.slf4j"                     %  "slf4j-api"                          % slf4JV,
  "ch.qos.logback"                %  "logback-classic"                    % logbackV          %  "test",
  // --- Testing ---
  "com.typesafe.akka"             %% "akka-http-testkit"                  % akkaHttpV         %  "test",
  "org.scalatest"                 %% "scalatest"                          % scalatestV        %  "test",
  "org.scalactic"                 %% "scalactic"                          % scalacticV        %  "test"
)
