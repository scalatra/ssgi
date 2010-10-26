package org.scalatra.ssgi

import scala.collection.Map
import java.io.{PrintWriter, InputStream}

/**
 * A representation of an HTTP request. Based on the Rack specification and Python WSGI (PEP 333).
 */
trait Request {
  /**
   * The HTTP request method, such as GET or POST
   */
  def requestMethod: HttpMethod

  /**
   * The initial portion of the request URL's "path" that corresponds to the application object, so that the
   * application knows its virtual "location". This may be an empty string, if the application corresponds to
   * the "root" of the server.
   */
  def scriptName: String

  /**
   * The remainder of the request URL's "path", designating the virtual "location" of the request's target within
   * the application. This may be an empty string, if the request URL targets the application root and does not have
   * a trailing slash.
   */
  def pathInfo: String

  /**
   * The portion of the request URL that follows the ?, if any. May be empty, but is always required!
   */
  def queryString: String

  /**
   * The contents of any Content-Type fields in the HTTP request, or None if absent.
   */
  def contentType: Option[String]

  /**
   * The contents of any Content-Length fields in the HTTP request, or None if absent.
   */
  def contentLength: Option[Int]

  /**
   * When combined with scriptName, pathInfo, and serverPort, these variables can be used to complete the URL.
   * Note, however, that the "Host" header, if present, should be used in preference to serverName for reconstructing
   * the request URL.
   */
  def serverName: String

  /**
   * When combined with scriptName, pathInfo, and serverName, these variables can be used to complete the URL.
   * See serverName for more details.
   */
  def serverPort: Int

  /**
   * The version of the protocol the client used to send the request. Typically this will be something like "HTTP/1.0"
   * or "HTTP/1.1" and may be used by the application to determine how to treat any HTTP request headers.
   */
  def serverProtocol: String

  /**
   * A map corresponding to the client-supplied HTTP request headers.
   *
   * TODO If the header is absent from the request, should get return Some(Seq.empty) or None?
   */
  def headers: Map[String, Seq[String]]

  /**
   * A tuple of the major and minor version of SSGI.
   */
  def ssgiVersion: (Int, Int) = SsgiVersion

  /**
   * A string representing the "scheme" portion of the URL at which the application is being invoked. Normally, this
   * will have the value "http" or "https", as appropriate.
   */
  def scheme: String

  /**
   * An input stream from which the HTTP request body can be read. (The server or gateway may perform reads on-demand
   * as requested by the application, or it may pre-read the client's request body and buffer it in-memory or on disk,
   * or use any other technique for providing such an input stream, according to its preference.)
   *
   * It is the responsibility of the caller to close the input stream.
   */
  def inputStream: InputStream

  /**
   * A print writer to which error output can be written, for the purpose of recording program or other errors in a
   * standardized and possibly centralized location.
   */
  def errors: PrintWriter = new PrintWriter(System.err)

  /**
   * A map in which the server or application may store its own data.
   */
  def attributes: Map[String, Any] = Map.empty
}