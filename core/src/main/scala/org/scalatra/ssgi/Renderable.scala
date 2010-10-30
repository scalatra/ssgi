package org.scalatra.ssgi

import scala.xml.NodeSeq
import java.nio.charset.Charset
import annotation.tailrec
import java.io._
import java.nio.channels.WritableByteChannel

trait Renderable {
  def writeTo(out: OutputStream, charset: Charset): Unit
}

/**
 * Extension for character-based types that can be more efficiently rendered to a Writer.
 */
trait CharRenderable extends Renderable {
  def writeTo(writer: Writer): Unit
}

trait InputStreamRenderable extends Renderable {
  def in: InputStream

  def writeTo(out: OutputStream, cs: Charset) {
    val in = this.in // it's a def, and we only want one
    val buf = new Array[Byte](4096)
    @tailrec
    def loop() {
      val n = in.read(buf)
      if (n >= 0) {
        out.write(buf, 0, n)
        loop()
      }
    }
    loop()
    in.close()
  }
}

/**
 * Extension for types that exist as a File.  This is useful primarily for zero-copy optimizations.
 */
trait FileRenderable extends InputStreamRenderable {
  def file: File

  def in: InputStream = new FileInputStream(file)
}

object Renderable {
  implicit def byteTraversableToRenderable(bytes: Traversable[Byte]) = new Renderable {
    def writeTo(out: OutputStream, cs: Charset) { for (b <- bytes) out.write(b) }
  }

  implicit def byteArrayToRenderable(bytes: Array[Byte]) = new Renderable {
    def writeTo(out: OutputStream, cs: Charset) { out.write(bytes) }
  }

  implicit def stringToRenderable(string: String) = new CharRenderable {
    def writeTo(out: OutputStream, cs: Charset) { out.write(string.getBytes(cs)) }
    def writeTo(writer: Writer) { writer.write(string) }
  }

  implicit def nodeSeqToRenderable(nodeSeq: NodeSeq) = stringToRenderable(nodeSeq.toString)

  implicit def inputStreamToRenderable(input: InputStream) = new InputStreamRenderable { def in = input }

  implicit def fileToRenderable(theFile: File) = new FileRenderable { def file = theFile }
}