package org.scalatra.ssgi

/**
 * Builds a Response.
 */
trait ResponseBuilder {
  import ResponseBuilder._

  /**
   * Returns the response.
   */
  def apply(): Response[_] = response

  protected var response: Response[_] = Response.Ok

  /**
   * Gets the status of the response
   */
  def status = response.status

  /**
   * Sets the status of the response
   *
   * @param statusCode the status code for this response
   */
  def status_=(statusCode: Int) = {
    response = response.copy(statusCode)
  }

  /**
   * Sets a header to the response. This replaces an existing header if it was there previously
   *
   * @param name the name of the header
   * @param value the value of the header
   */
  def setHeader(name: String, value: String): Unit =
    response = response.copy(headers = response.headers + (name -> value))

  /**
   * Sets the status code of the response to the code provided.
   * This'd better be a 4xx or 5xx status!
   *
   * @param code The numeric status code of the error
   */
  def sendError(code: Int): Unit = {
    if (code < 400) throw new Exception("This needs to be a status code > 400")
    status = code
  }

  /**
   * Sets the status code of the response to the code provided.
   * This'd better be a 4xx or 5xx status!
   *
   * @param code The numeric status code of the error
   * @param message The body of the error response
   */
  def sendError(code: Int, message: String): Unit = {
    sendError(code)
    body = message
  }

  /**
   * Gets the content type that belongs to this response
   */
  def contentType: String = response.headers.get("Content-Type") getOrElse DefaultContentType

  /**
   * Sets the content type of this response
   *
   * @param contentType The content type to use for this response
   */
  def contentType_=(contentType: String): Unit = setHeader("Content-Type", contentType)

  /**
   * Get the character encoding that belongs to this response
   */
  def characterEncoding: String = {
    val ct = contentType
    val start = ct.indexOf("charset=")
    if(start > 0){
      val encStart = start + 8
      val end = ct.indexOf(" ", encStart)
      if(end > 0) ct.substring(encStart, end)
      else ct.substring(encStart)
    } else {
      DefaultEncoding
    }
  }

  /**
   * Sets the character encoding of the response
   *
   * @param encoding The encoding to use for this response
   */
  def characterEncoding_=(encoding: String): Unit = {
    //TODO: make this more sensible? There might be more content in there than just a charset
    val newType = contentType.split(";").head + "; charset=" + encoding
    response = response.copy(headers = response.headers + ("Content-Type" -> newType))
    setHeader("Content-Type", newType)
  }

  /**
   * Sets the status to 302 and adds the location header for the redirect
   *
   * @param url the URL to redirect to
   */
  def sendRedirect(url: String): Unit = {
    val redirectHeaders = response.headers + ("Location" -> encodeRedirectUrl(url))
    response = response.copy(302, redirectHeaders)
  }

  protected def encodeRedirectUrl(url: String): String

  def body: Any = response.body

  // TODO SsgiServletResponse currently assumes a Response[Array[Byte]].  Undo that assumption.
  def body_=[A <% Renderable](body: A): Unit = response = response.copy(body = body)
}

object ResponseBuilder {
  val DefaultContentType: String = "text/plain"
  val DefaultEncoding: String = "utf-8"
}