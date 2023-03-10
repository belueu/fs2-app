ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

import sbt.Keys._

lazy val root = (project in file("."))
  .settings(
    name := "fs2-app",
    idePackagePrefix := Some("io.belueu.app")
  )

val Fs2Version = "3.2.4"
libraryDependencies += "co.fs2" %% "fs2-core" % Fs2Version
