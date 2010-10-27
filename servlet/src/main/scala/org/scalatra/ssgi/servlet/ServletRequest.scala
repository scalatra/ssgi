package org.scalatra.ssgi
package servlet

import javax.servlet.http.{HttpServletRequest, HttpServletRequestWrapper}
import scala.collection.immutable.DefaultMap
import scala.collection.JavaConversions._
import java.io.InputStream
import java.util.Enumeration
import scala.collection.{Iterator, Map}
import collection.mutable. {MapLike, Map => MMap}

/**
 * An HttpServletRequestWrapper that also implements the SSGI request interface.
 */
class ServletRequest(private val r: HttpServletRequest) extends HttpServletRequestWrapper(r) with Request {
  lazy val requestMethod: HttpMethod = HttpMethod(getMethod)

  lazy val scriptName: String = getContextPath + getServletPath

  lazy val pathInfo: String = Option(getPathInfo).getOrElse("")

  lazy val queryString: String = Option(getQueryString).getOrElse("")

  lazy val contentType: Option[String] = Option(getContentType)

  lazy val contentLength: Option[Int] = if (getContentLength >= 0) Some(getContentLength) else None

  def serverName: String = getServerName

  def serverPort: Int = getServerPort

  def serverProtocol: String = getProtocol

  lazy val headers: Map[String, Seq[String]] = new DefaultMap[String, Seq[String]] {
    def get(name: String): Option[Seq[String]] = getHeaders(name) match {
      case null => None
      case xs => Some(xs.asInstanceOf[Enumeration[String]].toSeq)
    }

    def iterator: Iterator[(String, Seq[String])] =
      getHeaderNames.asInstanceOf[Enumeration[String]] map { name => (name, apply(name)) }
  }

  def scheme: String = getScheme

  def inputStream: InputStream = getInputStream

  override lazy val attributes: MMap[String, Any] = new MMap[String, Any] {
    def get(name: String): Option[Any] = Option(getAttribute(name))

    def iterator: Iterator[(String, Any)] =
      getAttributeNames.asInstanceOf[Enumeration[String]] map { name => (name, getAttribute(name)) }

    def +=(kv: (String, Any)) = {
      setAttribute(kv._1, kv._2)
      this
    }

    def -=(name: _root_.scala.Predef.String) = {
      removeAttribute(name)
      this
    }
  }
}
