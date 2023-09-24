package org.robmaksoftware.config

case class Port(value: Int) extends AnyVal

case class AppConf(
    port: Port,
    logHttp: Boolean
)
