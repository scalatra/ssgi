package org.scalatra.ssgi

/**
 * An application is a function that takes exactly one argument, a request, and returns a response with a body of
 * type Traversable[Byte].
 */
trait Application extends (Request => Response[Traversable[Byte]])