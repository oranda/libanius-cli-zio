organization := "com.github.oranda"
name := "libanius-cli-zio"
version := "0.5"

val ZIOVersion = "2.0.0-RC5"

scalaVersion := "3.1.3-RC2"

resolvers ++= Seq("mvnrepository" at "https://mvnrepository.com/artifact/")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.0",
  "com.github.oranda" % "libanius_3" % "0.9.9.2",
  "dev.zio" % "zio_3" % ZIOVersion,
  "dev.zio" % "zio-test_3" % ZIOVersion % "test",
  "dev.zio" % "zio-test-sbt_3" % ZIOVersion % "test",
  "org.scalatest" %% "scalatest" % "3.2.12"
)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

scalacOptions ++= Seq("-new-syntax", "-rewrite", "-feature")

scalafmtOnCompile := false
