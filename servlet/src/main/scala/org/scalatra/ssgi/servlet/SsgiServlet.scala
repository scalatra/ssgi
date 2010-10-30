package org.scalatra.ssgi
package servlet

import javax.servlet.ServletConfig
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.nio.charset.Charset

class SsgiServlet extends HttpServlet {

  override def init(config: ServletConfig) {

  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) = {
    if(! resp.isCommitted) {
      // This doesn't work it's just an example
      val app = new Application[Array[Byte]]{ def apply(r: Request) = Response(200, Map.empty, Array[Byte]())}
      val response = app(new ServletRequest(req))
      resp.setStatus(response.status)
      response.headers foreach { case (key, value) => resp.addHeader(key, value) }
      val encoding = Charset.forName(resp.getCharacterEncoding)
      (response.body:Renderable) match {
        case cr: CharRenderable => cr.writeTo(resp.getWriter)
        case r: Renderable => r.writeTo(resp.getOutputStream, encoding)
      }
    }
  }
}