scalaVersion := "2.11.5"

val reactiveLibs = Seq(
  "io.reactivex" % "rxjava" % "1.0.8",
  "io.reactivex" % "rxnetty" % "0.4.8",
  "io.reactivex" % "rxjava-async-util" % "0.21.0",
  "io.reactivex" %% "rxscala" % "0.24.0")

val supportLibs = Seq(
  "com.google.guava" % "guava" % "18.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.2")

val testLibs = Seq(
  "org.scalatest" %% "scalatest" % "2.2.4",
  "org.scalacheck" %% "scalachece k" % "1.12.2"
) map (_ % "test")

libraryDependencies ++= reactiveLibs ++ supportLibs ++ testLibs
