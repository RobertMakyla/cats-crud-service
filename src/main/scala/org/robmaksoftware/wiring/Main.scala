package org.robmaksoftware.wiring

import cats.effect._
import org.robmaksoftware.dao.Dao
import org.robmaksoftware.service.PersonService
import org.robmaksoftware.http.HandlerImpl
import org.robmaksoftware.http.{Resource => HttpResource}
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.Port
import org.http4s.server.middleware.Logger
import org.robmaksoftware.config.AppConf
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig._
import pureconfig.generic.auto._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {

    val server: Resource[IO, Unit] = for {

      dao <- Dao.sqliteDao[IO]

      config <- IO.fromEither(ConfigSource.default.load[AppConf].left.map(e => new RuntimeException(e.prettyPrint()))).toResource

      service     = PersonService(dao)
      httpHandler = new HandlerImpl(service)
      logger      = Slf4jLogger.getLoggerFromName[IO]("httpLogger")
      routes      = new HttpResource[IO]().routes(httpHandler)

      routesWithLoggingOpt =
        if (config.logHttp) {
          Logger.httpRoutes[IO](
            logHeaders = true,
            logBody    = true,
            logAction  = Some(msg => logger.info(msg))
          )(routes)
        } else routes

      defaultServerBuilder = EmberServerBuilder
        .default[IO]
        .withHttpApp(routesWithLoggingOpt.orNotFound)

      serverBuilder = Port.fromInt(config.port.value).fold(defaultServerBuilder)(defaultServerBuilder.withPort)

      server <- serverBuilder.build
    } yield ()

    server
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
