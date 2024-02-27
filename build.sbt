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
      "org.http4s" %% "http4s-ember-server"        % http4sVersion,
      "org.http4s" %% "http4s-ember-client"        % http4sVersion,

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

      // logs
      "org.typelevel" %% "log4cats-core" % "2.6.0", // Only if you want to Support Any Backend
      "org.typelevel" %% "log4cats-slf4j" % "2.6.0", // Direct Slf4j Support - Recommended
      "ch.qos.logback" % "logback-classic" % "1.3.7" exclude ("org.slf4j", "slf4j"),

      // doobie - JDBC layer for Scala/Cats
      "org.tpolecat" %% "doobie-core"      % doobieVersion,
      "org.tpolecat" %% "doobie-hikari"    % doobieVersion,          // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,          // Postgres driver 42.6.0 + type mappings.
      "org.tpolecat" %% "doobie-specs2"    % doobieVersion % "test", // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test",  // ScalaTest support for typechecking statements.

      // SQLITE - in memory light db
      "org.xerial"     % "sqlite-jdbc" % "3.42.0.0",

      // version controlled db schema evolution (migration from SQL files)
      "org.flywaydb" % "flyway-core" % "9.20.1",

      // config
      "com.github.pureconfig" %% "pureconfig" % "0.17.4",

      //circe golden testing
      "io.circe" %% "circe-golden" % "0.2.1" % Test // Circe golden testing ( to make sure that changing domain classes/codecs won't mess with encoded data persisted in the DB)

    ) ++ Seq(
      "io.circe" %% "circe-core", // JSON parsing library for Scala - powered by Cats (required by guardrail)
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-generic-extras", //parse JSON in different formats: snake_case, kebab-case, PascalCase or SCREAMING_SNAKE_CASE  (configurable with implicit val Configuration)
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),

    Compile / guardrailTasks := List(
      ScalaServer(
        file("openapi/people.yaml"),
        pkg = "org.robmaksoftware.http",
        framework = "http4s",
        tracing = false,
        customExtraction = false,
        authImplementation = Disable,
        imports = List(
          "org.robmaksoftware.circe._"
        )
      )
    ),
    addCommandAlias("compileAll", ";+compile;+Test/compile" /* ;+integration-tests/compile */)
  )

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1") // Define implicits (implicit0) in for-comprehensions or matches