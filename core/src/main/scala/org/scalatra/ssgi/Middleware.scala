package org.scalatra.ssgi

trait Middleware  extends (Request => Response[_]) { self: Application =>   
}
