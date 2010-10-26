package org.scalatra.ssgi

import org.slf4j.{LoggerFactory, Logger}
import java.io.{StringWriter, PrintWriter}
import java.net.{InetAddress, UnknownHostException}

trait Logging {
  @transient @volatile protected[scalatra] var log = LoggerFactory.getLogger(getClass)
}

/**
 * LoggableException is a subclass of Exception and can be used as the base exception
 * for application specific exceptions.
 * <p/>
 * It keeps track of the exception is logged or not and also stores the unique id,
 * so that it can be carried all along to the client tier and displayed to the end user.
 * The end user can call up the customer support using this number.
 *
 * @author <a href="http://jonasboner.com">Jonas Bon&#233;r</a>
 */
class LoggableException extends Exception with Logging {
  private val uniqueId = getExceptionID
  private var originalException: Option[Exception] = None
  private var isLogged = false

  def this(baseException: Exception) = {
    this()
    originalException = Some(baseException)
  }

  def logException = synchronized {
    if (!isLogged) {
      originalException match {
        case Some(e) => log.error("Logged Exception [%s] %s", uniqueId, getStackTraceAsString(e))
        case None => log.error("Logged Exception [%s] %s", uniqueId, getStackTraceAsString(this))
      }
      isLogged = true
    }
  }

  private def getExceptionID: String = {
    val hostname: String = try {
      InetAddress.getLocalHost.getHostName
    } catch {
      case e: UnknownHostException =>
        log.error("Could not get hostname to generate loggable exception")
        "N/A"
    }
    hostname + "_" + System.currentTimeMillis
  }

  private def getStackTraceAsString(exception: Throwable): String = {
    val sw = new StringWriter
    val pw = new PrintWriter(sw)
    exception.printStackTrace(pw)
    sw.toString
  }
}