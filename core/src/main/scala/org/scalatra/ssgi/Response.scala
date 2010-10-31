package org.scalatra.ssgi

/**
 * An HTTP response.  Based on the Rack specification and Python WSGI (PEP-333).
 *
 * @param status The HTTP status.  Must be greater than or equal to 100.
 *
 * @param headers The header must not contain a Status key, contain keys with : or newlines in their name, contain
 * key names that end in - or _, but only contain keys that consist of letters, digits, _ or - and start with a letter.
 * The values of the header must consist of lines (for multiple header values, e.g. multiple Set-Cookie values)
 * separated by "\n". The lines must not contain characters below 037.  There must be a Content-Type, except when the
 * Status is 1xx, 204 or 304, in which case there must be none given.  There must not be a Content-Length header when
 * the Status is 1xx, 204 or 304.
 *
 * @param body The response body.  Should be transformed to a Traversable[Byte] before returning to the web server.
 */
case class Response[+A](status: Int = 200, headers: Map[String, String] = Map.empty, body: A)
                       (implicit renderer: A => Renderable) {
  /**
   * Returns a response by applying a function to this response's body.  The new response has the same status and
   * headers, and its body is the result of the function.
   */
  def map[B <% Renderable](f: A => B): Response[B] = copy(body = f(body))

  /**
   * Returns a new response by applying a function to this response's body.
   */
  def flatMap[B <% Renderable](f: A => Response[B]): Response[B] = f(body)

  def renderableBody: Renderable = renderer(body)
}
