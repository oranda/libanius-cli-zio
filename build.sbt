organization := "com.github.oranda"
name := "libanius-cli-zio"
version := "0.2"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.4",
  "dev.zio" %% "zio" % "1.0.0"
)

dependencyOverrides += "com.lihaoyi" %% "fastparse" % "1.0.0"

scalacOptions ++= Seq("-Ywarn-unused", "-Yrangepos")

addCompilerPlugin(scalafixSemanticdb)

