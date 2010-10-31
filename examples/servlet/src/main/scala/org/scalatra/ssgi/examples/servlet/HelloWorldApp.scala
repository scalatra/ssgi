package org.scalatra.ssgi
package examples.servlet

import scala.xml.NodeSeq

class HelloWorldApp extends Application[NodeSeq] {
  def apply(v1: Request): Response[NodeSeq] = Response(body = <h1>Hello, world!</h1>)
}