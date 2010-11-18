package org.scalatra.ssgi.servlet

import javax.servlet.ServletOutputStream
import java.io.ByteArrayOutputStream

class ByteArrayServletOutputStream(bufSize: Int) extends ServletOutputStream {

  def this() = this(32)
  
  val internal = new ByteArrayOutputStream

  def toByteArray = internal.toByteArray

  override def write( i: Int) { internal.write(i) }

  override def write( bytes: Array[Byte]) { internal.write(bytes) }

  override def write( bytes: Array[Byte], start: Int, end: Int) { internal.write(bytes, start, end) }

  override def flush { }

  override def close {  }

  private[ssgi] def reallyClose { internal.close }

  def size = internal.size

  def reset(): Unit = internal.reset()
}