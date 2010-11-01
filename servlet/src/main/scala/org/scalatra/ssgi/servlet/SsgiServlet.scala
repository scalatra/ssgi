package org.scalatra.ssgi
package servlet

import javax.servlet.{ServletException, ServletConfig}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.nio.charset.Charset

class SsgiServlet extends HttpServlet {
  import SsgiServlet._

  private var application: Application = _

  override def init(config: ServletConfig) {
    config.getInitParameter(ApplicationClassName) match {
      case className: String => loadApplication(className)
      case _ => throw new ServletException(ApplicationClassName + " should be set to class name of application")
    }
  }

  protected def loadApplication(className: String) {
    val appClass = getClass.getClassLoader.loadClass(className)
    application = appClass.newInstance.asInstanceOf[Application]
  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) = {
    val ssgiResponse = application(new ServletRequest(req))
    resp.setStatus(ssgiResponse.status)
    ssgiResponse.headers foreach { case (key, value) => resp.addHeader(key, value) }
    ssgiResponse.renderableBody match {
      case cr: CharRenderable => cr.writeTo(resp.getWriter)
      case r: Renderable => r.writeTo(resp.getOutputStream, Charset.forName("utf-8"))
    }
  }
}

object SsgiServlet {
  /**
   * The name of the init-param used to load the application
   */
  val ApplicationClassName = "org.scalatra.ssgi.Application"
}