package org.robmaksoftware.dao

import doobie.util.log.{ExecFailure, LogHandler, ProcessingFailure, Success}
import org.slf4j.LoggerFactory

/*
   it's copied from the default doobie.util.log.LogHandler.jdkLogHandler
   but instead of using java.util.Logger inside,
   I'm using wrapped slf4j Logger to be able to
   configure it with logback.xml (logback-test.xml)
 */
object MyLogHandler {

  val mySlf4jLogHandler: LogHandler = {
    val logger = LoggerFactory.getLogger(getClass)
    LogHandler {

      case Success(s, a, e1, e2) =>
        logger.info(s"""Successful Statement Execution:
             |
             |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
             |
             | arguments = [${a.mkString(", ")}]
             |   elapsed = ${e1.toMillis.toString} ms exec + ${e2.toMillis.toString} ms processing (${(e1 + e2).toMillis.toString} ms total)
        """.stripMargin)

      case ProcessingFailure(s, a, e1, e2, t) =>
        logger.error(s"""Failed Resultset Processing:
             |
             |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
             |
             | arguments = [${a.mkString(", ")}]
             |   elapsed = ${e1.toMillis.toString} ms exec + ${e2.toMillis.toString} ms processing (failed) (${(e1 + e2).toMillis.toString} ms total)
             |   failure = ${t.getMessage}
        """.stripMargin)

      case ExecFailure(s, a, e1, t) =>
        logger.error(s"""Failed Statement Execution:
             |
             |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
             |
             | arguments = [${a.mkString(", ")}]
             |   elapsed = ${e1.toMillis.toString} ms exec (failed)
             |   failure = ${t.getMessage}
        """.stripMargin)

    }
  }
}
