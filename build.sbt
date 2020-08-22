organization := "com.github.oranda"
name := "libanius-cli-zio"
version := "0.1.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.4",
  "dev.zio" %% "zio" % "1.0.0-RC11-1"
)

dependencyOverrides += "com.lihaoyi" %% "fastparse" % "1.0.0"

