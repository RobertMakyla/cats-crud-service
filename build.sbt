
val catsVersion = "2.4.2"

val scalaTestVersion = "3.2.16"


lazy val root = project
  .in(file("."))
  .settings(
    name := "cats-crud-service",
    version := "0.1.0",

    scalaVersion := "2.13.6",

    libraryDependencies ++= Seq(

      // cats
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-free" % catsVersion,
      "org.typelevel" %% "cats-effect" % "3.2.0",
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test,

      // enum
      "com.beachape" %% "enumeratum" % "1.7.0",

      // test
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    )
  )