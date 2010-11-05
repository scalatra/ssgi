package org.scalatra.ssgi

import java.util.Locale

sealed trait HttpMethod {
  def isSafe: Boolean
}
case object Options extends HttpMethod {
  def isSafe = true
}
case object Get extends HttpMethod {
  def isSafe = true
}
case object Head extends HttpMethod {
  def isSafe = true
}
case object Post extends HttpMethod {
  def isSafe = false
}
case object Put extends HttpMethod {
  def isSafe = false
}
case object Delete extends HttpMethod {
  def isSafe = false
}
case object Trace extends HttpMethod {
  def isSafe = true
}
case object Connect extends HttpMethod {
  def isSafe = false
}
case class ExtensionMethod(name: String) extends HttpMethod {
  def isSafe = false
}

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

  val methods: Set[HttpMethod] = methodMap.values.toSet
}
