package org.scalatra.ssgi

/**
 * An application is a function that takes exactly one argument, a request, and returns a response.
 */
trait Application[+A] extends (Request => Response[A])

object Application {
  implicit def apply[A](f: Request => Response[A]) = new Application[A]{ def apply(req: Request) = f(req) }
}