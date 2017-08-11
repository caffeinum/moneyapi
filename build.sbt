import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.caffeinum.moneyapi",
      scalaVersion := "2.12.1",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Main",
    libraryDependencies += scalaTest % Test
  )

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.16.0-M1",
  "com.github.finagle" %% "finch-circe" % "0.16.0-M1",
  "com.twitter" %% "twitter-server" % "1.30.0"
)
