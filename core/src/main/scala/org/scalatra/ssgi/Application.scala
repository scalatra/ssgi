package org.scalatra.ssgi

/**
 * An application is a function that takes exactly one argument, a request, and returns a response.
 */
trait Application extends (Request => Response[_])

object Application {
  implicit def apply(f: Request => Response[_]) = new Application { def apply(req: Request) = f(req) }
}