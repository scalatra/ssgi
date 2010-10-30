package org.scalatra.ssgi

import scala.xml.NodeSeq
import java.nio.charset.Charset
import annotation.tailrec
import java.io.{File, FileInputStream, InputStream, OutputStream}

trait Renderable {
  def writeTo(out: OutputStream, charset: Charset): Unit
}

object Renderable {
  implicit def byteTraversableToRenderable(bytes: Traversable[Byte]) = new Renderable {
    def writeTo(out: OutputStream, cs: Charset) { for (b <- bytes) out.write(b) }
  }

  implicit def byteArrayToRenderable(bytes: Array[Byte]) = new Renderable {
    def writeTo(out: OutputStream, cs: Charset) { out.write(bytes) }
  }

  implicit def stringToRenderable(string: String) = new Renderable {
    def writeTo(out: OutputStream, cs: Charset) { out.write(string.getBytes(cs)) }
  }

  implicit def nodeSeqToRenderable(nodeSeq: NodeSeq) = stringToRenderable(nodeSeq.toString)

  implicit def inputStreamToRenderable(in: InputStream) = new Renderable {
    def writeTo(out: OutputStream, cs: Charset) {
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

  implicit def fileToRenderable(file: File) = inputStreamToRenderable(new FileInputStream(file))
}