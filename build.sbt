
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

      // fs2
      "co.fs2" %% "fs2-core" % "3.7.0",

      // enum
      "com.beachape" %% "enumeratum" % "1.7.0",

      // test
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",

      // db
      "org.postgresql" % "postgresql" % "42.6.0",

      // doobie - JDBC layer for Scala/Cats
      "org.tpolecat" %% "doobie-core"      % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC4",          // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC4",          // Postgres driver 42.6.0 + type mappings.
      "org.tpolecat" %% "doobie-specs2"    % "1.0.0-RC4" % "test", // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC4" % "test",  // ScalaTest support for typechecking statements.

      // SQLITE - in memory light db
      "org.xerial"     % "sqlite-jdbc" % "3.42.0.0",

      // version controlled db schema evolution (migration from SQL files)
      "org.flywaydb" % "flyway-core" % "9.20.1"
    )
  )

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1") // Define implicits (implicit0) in for-comprehensions or matches