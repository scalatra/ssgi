package org.scalatra.ssgi

import java.util.Locale

sealed trait HttpMethod
case object Options extends HttpMethod
case object Get extends HttpMethod
case object Head extends HttpMethod
case object Post extends HttpMethod
case object Put extends HttpMethod
case object Delete extends HttpMethod
case object Trace extends HttpMethod
case object Connect extends HttpMethod
case class ExtensionMethod(name: String) extends HttpMethod

object HttpMethod {
  private val methodMap = Map(
    "OPTIONS" -> Options,
    "GET" -> Get,
    "HEAD" -> Head,
    "POST" -> Post,
    "PUT" -> Put,
    "DELETE" -> Delete,
    "TRACE" -> Trace,
    "CONNECT" -> Connect
  )

  def apply(name: String): HttpMethod = {
    val canonicalName = name.toUpperCase(Locale.ENGLISH)
    methodMap.getOrElse(canonicalName, ExtensionMethod(canonicalName))
  }
}
