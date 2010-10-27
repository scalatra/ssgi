package org.scalatra.ssgi.servlet

import javax.servlet.ServletConfig
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import org.scalatra.ssgi.{Response, Request, Application}

class SsgiServlet extends HttpServlet {

  override def init(config: ServletConfig) {

  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) = {
    if(! resp.isCommitted) {
      // This doesn't work it's just an example
      val app = new Application{ def apply(r: Request) = Response(200, Map.empty, Array[Byte]())} 
      val response = app(new ServletRequest(req))
      resp.setStatus(response.status)
      response.headers foreach { case (key, value) => resp.addHeader(key, value) }
      resp.getOutputStream.write(response.body.toArray)
    }
  }
}