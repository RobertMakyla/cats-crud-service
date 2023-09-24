package org.robmaksoftware.wiring

import cats.effect._
import org.robmaksoftware.dao.Dao
import org.robmaksoftware.service.PersonService
import org.robmaksoftware.http.HandlerImpl
import org.robmaksoftware.http.{Resource ⇒ HttpResource}
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._ // port"..." interpolator
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {

    val server: Resource[IO, Unit] = for {

      dao ← Dao.sqliteDao[IO]

      service     = PersonService(dao)
      httpHandler = new HandlerImpl(service)
      logger      = Slf4jLogger.getLoggerFromName[IO]("httpLogger")
      routes      = new HttpResource[IO]().routes(httpHandler)

      routesWithLogging = Logger.httpRoutes[IO](
        logHeaders = true,
        logBody    = true,
        logAction  = Some(msg ⇒ logger.info(msg))
      )(routes)

      defaultServerBuilder = EmberServerBuilder
        .default[IO]
        .withLogger(logger)
        .withHttpApp(routesWithLogging.orNotFound)

      // serverBuilder = Port.fromInt(port8080).fold(defaultServerBuilder)(defaultServerBuilder.withPort)
      serverBuilder = defaultServerBuilder.withPort(port"8080")

      server ← serverBuilder.build
    } yield ()

    server
      .use(_ ⇒ IO.never)
      .as(ExitCode.Success)
  }

}
