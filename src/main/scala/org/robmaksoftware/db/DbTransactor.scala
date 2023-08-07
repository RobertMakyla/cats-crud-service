package org.robmaksoftware.db

import cats.effect.{Async, Resource, Sync}
import cats.effect.syntax.resource._
import java.io.File

import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location


object DbTransactor {

  val driver = "orq.sqlite.JDBC"
  val user = "user"
  val pass = "s3cr3t"

  val flywayMigrationDir = "classpath:db-migration"

  val poolSize = 4


  def sqlite[F[_] : Async](flywayMigration: Boolean): Resource[F, Transactor[F]] =
    for {

      filePath <- tempDbFilePath.toResource
      url = s"jdbc:sqlite:$filePath"
      ec <- ExecutionContexts.fixedThreadPool[F](poolSize)
      transactor <- HikariTransactor.newHikariTransactor[F](driver, url, user, pass, ec)
      _ <- Sync[F].delay {
        flywayConfig(url, user, pass, flywayMigration)
          .load()
          .migrate()
      }.toResource
    } yield transactor

  private def tempDbFilePath[F[_] : Sync]: F[String] = Sync[F].blocking {
    val tmpFile = File.createTempFile("db", ".tmp")
    tmpFile.deleteOnExit()
    tmpFile.getPath
  }

  private def flywayConfig(url: String, user: String, pass: String, flywayMigration: Boolean) =
    Flyway
      .configure()
      .dataSource(url, user, pass)
      .outOfOrder(true) // if v1 and v3 are installed, it can still install v2
      .locations((if (flywayMigration) new Location(flywayMigrationDir) :: Nil else Nil): _*)


}
