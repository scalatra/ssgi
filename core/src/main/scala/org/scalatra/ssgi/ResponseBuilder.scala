package org.scalatra.ssgi

/**
 * Builds a Response.
 */
trait ResponseBuilder {
  /**
   * Returns the response.
   */
  def apply(): Response[_] = response

  protected var response = Response.Ok
}