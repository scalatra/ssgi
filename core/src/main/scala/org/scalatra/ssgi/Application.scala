package org.scalatra.ssgi

/**
 * An application is a function that takes exactly one argument, a request, and returns a response.
 */
trait Application[+A] extends (Request => Response[A])