package org.scalatra.ssgi
package examples.servlet

import scala.xml.NodeSeq

class HelloWorldApp extends Application {
  def apply(v1: Request) = Response(body = <h1>Hello, world!</h1>)
}