package org.scalatra.ssgi.servlet

import javax.servlet.http.{Cookie => ServletCookie, HttpServletResponse}
import java.lang.String
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import org.scalatra.ssgi.{CookieOptions, Cookie, Response}
import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone, Locale}

trait SsgiResponseWrapping {

  private[servlet] var _ssgiResponse: Response[Traversable[Byte]] = null

  def ssgiResponse = _ssgiResponse
  
}

object SsgiServletResponse {
  val DEFAULT_CONTENT_TYPE = "text/plain"
  val DEFAULT_ENCODING = "UTF-8"
  val DATE_FORMAT = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z")
  DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"))

}
class SsgiServletResponse extends HttpServletResponse with SsgiResponseWrapping {

  import SsgiServletResponse._

  def setStatus(statusCode: Int, body: String) = {
    _ssgiResponse = _ssgiResponse.copy(statusCode, body = body.getBytes(getCharacterEncoding))
  }

  def setStatus(statusCode: Int) = {
    _ssgiResponse = _ssgiResponse.copy(statusCode)
  }

  def addIntHeader(name: String, value: Int) = {
    addSsgiHeader(name, value.toString)
  }

  def setIntHeader(name: String, value: Int) = setHeader(name, value.toString)

  def addHeader(name: String, value: String) = {
    addSsgiHeader(name, value)
  }

  def setHeader(name: String, value: String) =
    _ssgiResponse = _ssgiResponse.copy(headers = _ssgiResponse.headers + (name -> value))

  def addDateHeader(name: String, value: Long) = {
    addHeader(name, formatDate(value))
  }

  def setDateHeader(name: String, value: Long) = {
    setHeader(name, formatDate(value))
  }

  def sendRedirect(url: String) = {
    val redirectHeaders = _ssgiResponse.headers + ("Location" -> encodeRedirectURL(url))
    _ssgiResponse = _ssgiResponse.copy(302, redirectHeaders )
  }

  def sendError(code: Int) = {
    sendError(code, null)
  }

  def sendError(code: Int, message: String) = {
    // TODO: Actually implement something useful here
    throw new NotImplementedException
  }

  def encodeRedirectUrl(url: String) = encodeURL(url)

  def encodeUrl(url: String) = encodeURL(url)

  def encodeRedirectURL(url: String) = encodeURL(url)

  def encodeURL(url: String) = {
    // TODO: implement this with taking into account the session id cookie and url encode stuff after '?'
    url
  }

  def containsHeader(key: String) = _ssgiResponse.headers.contains(key)

  def addCookie(servletCookie: ServletCookie) = {}

  def getLocale = _ssgiResponse.headers.get("Content-Language") match {
    case Some(locLang) => {
      locLang.split("-").toList match {
        case lang :: Nil => new Locale(lang)
        case lang :: country :: Nil => new Locale(lang, country)
        case lang :: country :: variant :: Nil => new Locale(lang, country, variant)
        case _ => Locale.getDefault
      }
    }
    case _ => Locale.getDefault
  }

  def setLocale(locale: Locale) = {
    setHeader("Content-Language", locale.toString.replace('_', '-'))
  }

  def reset = {
    // TODO: make this evil
  }

  def isCommitted = false

  def resetBuffer = {
    // TODO: make this evil
  }

  def flushBuffer = {
    // TODO: make this evil
  }

  def getBufferSize = 0

  def setBufferSize(size: Int) = {
    // TODO: make this evil
  }

  def setContentType(contentType: String) = setHeader("Content-Type", contentType)

  def setContentLength(contentLength: Int) = setHeader("Content-Length", contentLength.toString)

  def setCharacterEncoding(encoding: String) = {
    //TODO: make this more sensible? There might be more content in there than just a charset
    val newType = getContentType.split(";").head + "; charset=" + encoding
    setHeader("Content-Type", newType)
  }

  def getWriter = null

  def getOutputStream = null

  def getContentType = _ssgiResponse.headers.get("Content-Type") getOrElse DEFAULT_CONTENT_TYPE

  def getCharacterEncoding = {
    val ct = getContentType
    val start = ct.indexOf("charset=")
    if(start > 0){
      val end = ct.indexOf(" ", start + 1)
      ct.substring(start + 1, end)
    } else {
      DEFAULT_ENCODING
    }
  }

  private def addSsgiHeader(name: String, value: String) = {
   val headers =  _ssgiResponse.headers.get(name) match {
      case Some(hdrVal) => _ssgiResponse.headers + (name -> "%s,%s".format(hdrVal, value))
      case _ => _ssgiResponse.headers + (name -> value.toString)
    }
    _ssgiResponse = _ssgiResponse.copy(headers = headers)
  }

  private def formatDate(millis: Long) = {
    val cal = Calendar.getInstance
    cal.setTimeInMillis(millis)
    DATE_FORMAT.format(cal.getTime)
  }

  private def servletCookie2Cookie(sc: ServletCookie) = Cookie(sc.getName, sc.getValue)(CookieOptions(
     sc.getDomain,
     sc.getPath,
     sc.getMaxAge,
     sc.getSecure,
     sc.getComment,
     sc.getVersion))
}