import dev.guardrail.AuthImplementation.Disable

val catsVersion = "2.4.2"

val doobieVersion = "1.0.0-RC2"

val circeVersion = "0.14.1"

val http4sVersion = "0.23.15"

lazy val root = project
  .in(file("."))
  .enablePlugins(GuardrailPlugin)
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

      // http4s - http for scala powered by Cats
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,

      // streams fs2
      "co.fs2" %% "fs2-core" % "3.7.0",

      // enum
      "com.beachape" %% "enumeratum" % "1.7.0",

      // test
      "org.scalatest"  %% "scalatest"  % "3.2.16" % "test", // specs, matchers, fixtures
      "org.scalacheck" %% "scalacheck" % "1.17.0" % "test", // property based testing, generators
      "org.typelevel" %% "scalacheck-effect" % "1.0.4",
      "org.scalatestplus" %% "scalacheck-1-17" % "3.2.16.0" % "test", // effectless property based testing : SimplePropsSpec

      // db
      "org.postgresql" % "postgresql" % "42.6.0",

      // doobie - JDBC layer for Scala/Cats
      "org.tpolecat" %% "doobie-core"      % doobieVersion,
      "org.tpolecat" %% "doobie-hikari"    % doobieVersion,          // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,          // Postgres driver 42.6.0 + type mappings.
      "org.tpolecat" %% "doobie-specs2"    % doobieVersion % "test", // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test",  // ScalaTest support for typechecking statements.

      // SQLITE - in memory light db
      "org.xerial"     % "sqlite-jdbc" % "3.42.0.0",

      // version controlled db schema evolution (migration from SQL files)
      "org.flywaydb" % "flyway-core" % "9.20.1"

    ) ++ Seq(
      "io.circe" %% "circe-core", // JSON parsing library for Scala - powered by Cats (required by guardrail)
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),

    Compile / guardrailTasks := List(
      ScalaServer(
        file("openapi/people.yaml"),
        pkg = "org.robmaksoftware.people.http",
        framework = "http4s",
        tracing = false,
        customExtraction = false,
        authImplementation = Disable,
        imports = List(
          "org.robmaksoftware.circe._"
        )
      )
    )
  )

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1") // Define implicits (implicit0) in for-comprehensions or matches