package org.scalatra.ssgi
package examples.servlet

import scala.xml.NodeSeq

trait BodyUppercaser extends Middleware { self: Application => 
  abstract override def apply(v1: Request): Response[_] = {
    val resp = super.apply(v1)
    resp.copy(body = <h1>(resp.body \ "h1").text.toUpperCase</h1>)
  }
}

class HelloWorldApp extends Application with BodyUppercaser {
  def apply(v1: Request): Response[_] = Response(body = <h1>Hello, world!</h1>)
}
