
organization := "com.github.oranda"
name := "libanius-cli-zio"
version := "0.3.3"

scalaVersion := "2.12.12"

val ZIOVersion = "1.0.0"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.4",
  "dev.zio" %% "zio" % ZIOVersion,
  "com.github.oranda" %% "libanius" % "0.9.8.7.2",
  "dev.zio" %% "zio-test" % ZIOVersion % "test",
  "dev.zio" %% "zio-test-sbt" % ZIOVersion % "test",
  "dev.zio" %% "zio-logging-slf4j" % "0.5.3"
)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

dependencyOverrides += "com.lihaoyi" %% "fastparse" % "1.0.0"

scalacOptions ++= Seq("-Ywarn-unused", "-Yrangepos")

addCompilerPlugin(scalafixSemanticdb)

scalafmtOnCompile := true

