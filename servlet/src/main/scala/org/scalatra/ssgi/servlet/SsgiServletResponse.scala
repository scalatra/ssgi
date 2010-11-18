package org.scalatra.ssgi
package servlet

import javax.servlet.http.{Cookie => ServletCookie, HttpServletResponse}
import java.lang.String
import java.util.{Calendar, TimeZone, Locale}
import java.io.{PrintWriter}
import org.apache.commons.lang.time.FastDateFormat
import java.nio.charset.Charset

object SsgiServletResponse {
  val DATE_FORMAT = FastDateFormat.getInstance("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", TimeZone.getTimeZone("GMT"))

}

/**
 * A wrapper for a HttpServletResponse that builds up an SSGI Response.
 * This class makes the reset operations NOOP's
 */
class SsgiServletResponse(private val r: HttpServletResponse) extends HttpServletResponse with ResponseBuilder {

  import SsgiServletResponse._

  private var _out = new ByteArrayServletOutputStream
  private var _pw = new PrintWriter(_out)

  /**
   * Returns the SSGI Response with the default headers added if necessary
   */
  override def apply() = {
    val hdrs = generateDefaultHeaders
    _pw.flush
    Response(status, hdrs, _out.toByteArray)
  }

  /**
   * Sets the status of the response
   *
   * @param statusCode the status code for this response
   * @param body the message for the body
   */
  def setStatus(statusCode: Int, body: String) {
    _pw.write(body)
    response = response.copy(statusCode)
  }

  /**
   * Sets the status of the response
   *
   * @param statusCode the status code for this response
   */
  def setStatus(statusCode: Int): Unit = status = statusCode

  /**
   * Adds an int header to the response
   *
   * @param name the name of the header
   * @param value the value of the header
   */
  def addIntHeader(name: String, value: Int) = {
    addHeader(name, value.toString)
  }

  /**
   * Sets an int header to the response. This replaces an existing header if it was there previously
   *
   * @param name the name of the header
   * @param value the value of the header
   */
  def setIntHeader(name: String, value: Int) = setHeader(name, value.toString)

  /**
   * Adds a header to the response
   *
   * @param name the name of the header
   * @param value the value of the header
   */
  def addHeader(name: String, value: String) = {
    addSsgiHeader(name, value)
  }

  /**
   * Adds a date header to the response. 
   *
   * @param name the name of the header
   * @param value the value of the header
   */
  def addDateHeader(name: String, value: Long) = {
    addHeader(name, formatDate(value))
  }

  /**
   * Sets a date header to the response. This replaces an existing header if it was there previously
   *
   * @param name the name of the header
   * @param value the value of the header
   */
  def setDateHeader(name: String, value: Long) = {
    setHeader(name, formatDate(value))
  }



  /**
   * Encodes the redirect url
   *
   * @param url the url to encode
   */
  def encodeRedirectUrl(url: String) = encodeURL(url)


  /**
   * Encodes a url
   *
   * @param url the url to encode
   */
  def encodeUrl(url: String) = encodeURL(url)

  /**
   * Encodes the redirect url
   *
   * @param url the url to encode
   */
  def encodeRedirectURL(url: String) = encodeURL(url)

  /**
   * Encodes a url
   *
   * @param url the url to encode
   */
  def encodeURL(url: String) = {
    r.encodeURL(url)
  }

  /**
   * Predicate to check if the response already contains the header with the specified key
   *
   * @param key The key to check the header collection for
   */
  def containsHeader(key: String) = response.headers.contains(key)

  /**
   * Adds a cookie to the response
   */
  def addCookie(servletCookie: ServletCookie) = {
    addHeader("Set-Cookie", servletCookie2Cookie(servletCookie).toCookieString)
  }

  /**
   * Gets the currently configured locale for this response
   */
  def getLocale = response.headers.get("Content-Language") match {
    case Some(locLang) => {
      locLang.split("-").toList match {
        case lang :: Nil => new Locale(lang)
        case lang :: country :: Nil => new Locale(lang, country)
        case lang :: country :: variant :: whatever => new Locale(lang, country, variant)
        case _ => Locale.getDefault
      }
    }
    case _ => Locale.getDefault
  }

  /**
   * Sets the locale for this response
   */
  def setLocale(locale: Locale) = {
    setHeader("Content-Language", locale.toString.toLowerCase(locale).replace('_', '-'))
  }

  /**
   * Resets this response completely discarding the current content and headers and sets the status to 200
   */
  def reset = {
    response = Response(200, Map.empty, Array[Byte]())
    _out.close
    _out = new ByteArrayServletOutputStream
    _pw = new PrintWriter(_out)
  }

  /**
   * Always returns false in the context of SSGI. Committing responses is a big NO-NO for supporting middlewares
   */
  def isCommitted = false

  /**
   * Resets the content of this response discarding the current content
   */
  def resetBuffer = {
    body = Array[Byte]()
    _out.close
    _out = new ByteArrayServletOutputStream
    _pw = new PrintWriter(_out)
  }

  /**
   * This is a NOOP The interface is legacy. It's preferred to use the SSGI Repsonse class
   */
  def flushBuffer = {

  }

  /**
   * This is a NOOP The interface is legacy. It's preferred to use the SSGI Repsonse class
   */
  def getBufferSize = Int.MaxValue

  /**
   * This is a NOOP The interface is legacy. It's preferred to use the SSGI Repsonse class
   */
  def setBufferSize(size: Int) = {

  }

  /**
   * Sets the content type of this response
   *
   * @param contentType The content type to use for this response
   */
  def setContentType(contentType: String): Unit = this.contentType = contentType

  /**
   * Sets the content length of this response
   *
   * @param contentLength the content length of this response
   */
  def setContentLength(contentLength: Int) = setHeader("Content-Length", contentLength.toString)

  /**
   * Sets the character encoding of the response
   *
   * @param encoding The encoding to use for this response
   */
  def setCharacterEncoding(encoding: String): Unit = characterEncoding = encoding

  /**
   * Gets the printwriter for this response
   */
  def getWriter = _pw

  /**
   * Get the output stream for this response
   * This wraps a ByteArrayOutputStream so be careful with huge responses.
   *
   *  Use the SSGI Response directly instead of this method if  you need to send a big response
   */
  def getOutputStream = _out

  /**
   * Gets the content type that belongs to this response
   */
  def getContentType: String = contentType

  /**
   * Get the character encoding that belongs to this response
   */
  def getCharacterEncoding: String = characterEncoding

  private def generateDefaultHeaders = {
    var headers = response.headers
    if(!headers.contains("Content-Type")) headers += "Content-Type" -> "%s; charset=%s".format(getContentType, getCharacterEncoding)
    if(!headers.contains("Content-Length")) {
      headers += "Content-Length" -> body.asInstanceOf[Array[Byte]].length.toString
    }
    if(!headers.contains("Content-Language")) headers += "Content-Language" -> getLocale.toString.replace('_', '-')
    if(!headers.contains("Date")) headers += "Date" -> formatDate(Calendar.getInstance.getTimeInMillis)
    headers
  }

  private def addSsgiHeader(name: String, value: String) = {
   val headers =  response.headers.get(name) match {
      case Some(hdrVal) => response.headers + (name -> "%s,%s".format(hdrVal, value))
      case _ => response.headers + (name -> value.toString)
    }
    response = response.copy(headers = headers)
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

  // TODO temporary hack around mismatch between streaming and in-memory bodies
  override def body: Any = _out.toByteArray
  override def body_=[A <% Renderable](body: A): Unit = {
    _out.reset()
    body.writeTo(_out, Charset.forName(characterEncoding))
  }
}