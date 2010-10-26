package org.scalatra.ssgi.servlet.testing

import java.net.URL
import org.scalatra.ssgi.MimeUtil
import collection.mutable.HashMap
import java.util.{ Vector => JVector }
import collection.JavaConversions._
import java.io.{BufferedInputStream, ByteArrayOutputStream, ByteArrayInputStream, InputStream}
import javax.servlet.{RequestDispatcher, Servlet, ServletContext}

class MockServletContext(initialContextPath: String) extends ServletContext {

  private var contextPath = initialContextPath
  private var minorVersion = 5
  private var servletContextName = "MockServletContext"
  private val contexts =  new HashMap[String, ServletContext]()
  private val attributes =  new HashMap[String, Any]()
  private val initParameters =  new HashMap[String, String]()
  private val realPaths =  new HashMap[String, String]()
  private val resources = new HashMap[String, URL]()
  private val resourcePaths = new HashMap[String, Set[String]]()
  private val resourceStreams = new HashMap[String, Array[Byte]]()
  private val requestDispatchers = new HashMap[String, RequestDispatcher]()

  def this() = this("")


  def reset = {
    contextPath = initialContextPath
    minorVersion = 5
    servletContextName = "MockServletContext"
    contexts.clear
    attributes.clear
    initParameters.clear
    realPaths.clear
    resources.clear
    resourcePaths.clear
    resourceStreams.clear
    requestDispatchers.clear
  }

  def setContextPath(ctxtPath: String) = {
    contextPath = if (ctxtPath != null ) ctxtPath else ""
    this
  }

  def getContextPath() =  contextPath

  def registerContext(contextPath: String, context: ServletContext) = {
    contexts += contextPath -> context
  }

  def getContext(contextPath: String) = {
    if (this.contextPath == contextPath) {
      this
    } else {
     contexts(contextPath)
    }
  }

  def getMajorVersion() = 2

  def setMinorVersion(minorVersion: Int)  = {
    this.minorVersion = minorVersion
    this
  }

  def getMinorVersion() = minorVersion

  def getMimeType(filePath: String) = MimeUtil.mimeType(filePath)


  def getServerInfo = "Scalatra SSGI Mock Servlet Context"


  def getServletContextName =  servletContextName
  def setServletContextName(name: String) = {
    servletContextName = name
    this
  }

  def removeAttribute(key: String) { attributes -= key }
  def setAttribute(key: String, value: Any) = {
    attributes += key -> value
    this
  }
  def getAttributeNames = new JVector(attributes.keySet).elements
  def getAttribute(key: String) = attributes(key).asInstanceOf[AnyRef]

  def getInitParameterNames = new JVector(initParameters.keySet).elements
  def getInitParameter(key: String) = initParameters(key)
  def setInitParameter(key: String, param: String) = {
    initParameters += key -> param
    this
  }

  def getRealPath(path: String) = realPaths(path)
  def setRealPath(path: String, realPath: String) = {
    realPaths += path -> realPath
    this
  }

  def log(msg: String, th: Throwable) {}
  def log(msg: String) {}
  def log(exc: Exception, msg: String) {}

  def getServletNames = new JVector[String]().elements
  def getServlets = new JVector[String]().elements
  def getServlet(servletName: String) = null.asInstanceOf[Servlet]

  def getResource(path: String) = resources(path)
  def setResource(path: String, resource: URL) = {
    resources += path -> resource
    this
  }

  def getResourcePaths(path: String) = resourcePaths.get(path) getOrElse null.asInstanceOf[Set[String]]

  def addResourcePaths(path: String, resourcePaths: String*) = {
    val set = this.resourcePaths.get(path) getOrElse Set[String]()
    this.resourcePaths += path -> (resourcePaths.toSet ++ set)
    this
  }

  def getResourceAsStream(path: String) = {
    if(! resourceStreams.contains(path)) null.asInstanceOf[InputStream]
    else {
      val data = resourceStreams(path)
      new ByteArrayInputStream(data)
    }
  }
  
  private def streamToByteArray(is: InputStream): Array[Byte] = {
    val out = new ByteArrayOutputStream
    val data = new BufferedInputStream(is)
    var current = data.read
    while (current >= 0) {
      out.write(current)
      current = data.read
    }
    out.toByteArray
  }

  def setResourceAsStream(path: String, is: InputStream) = {
    resourceStreams += path -> streamToByteArray(is)
    this
  }

  def setResourceAsStream(path: String, is: Array[Byte]) = {
    resourceStreams += path -> is
    this
  }


  def getNamedDispatcher(name: String) = getRequestDispatcher(name)
  def getRequestDispatcher(path: String) = {
    requestDispatchers.get(path) match {
      case Some(disp) => disp
      case _ => {
        val disp = new MockRequestDispatcher()
        setRequestDispatcher(path, disp)
        disp
      }
    }
  }

  def setRequestDispatcher(path: String, dispatcher: RequestDispatcher) = {
    if(dispatcher.isInstanceOf[MockRequestDispatcher]) {
      dispatcher.asInstanceOf[MockRequestDispatcher].setPath(path)
    }
    requestDispatchers += path -> dispatcher
    this
  }

}