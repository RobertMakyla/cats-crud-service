package org.robmaksoftware.wiring

import cats.effect._
import org.robmaksoftware.dao.Dao
import org.robmaksoftware.service.PersonService
import org.robmaksoftware.http.HandlerImpl
import org.robmaksoftware.http.{Resource => HttpResource}
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.Port

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {

    val server: Resource[IO, Unit] = for {
      dao <- Dao.sqliteDao[IO]

      service     = PersonService(dao)
      httpHandler = new HandlerImpl(service)
      routes      = new HttpResource[IO]().routes(httpHandler)

      defaultServerBuilder = EmberServerBuilder
        .default[IO]
        .withHttpApp(routes.orNotFound)

      serverBuilder = Port.fromInt(8080).fold(defaultServerBuilder)(defaultServerBuilder.withPort)

      server <- serverBuilder.build
    } yield ()

    server
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
