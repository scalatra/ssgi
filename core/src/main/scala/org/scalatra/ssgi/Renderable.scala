package org.scalatra.ssgi

import scala.xml.NodeSeq
import java.nio.charset.Charset
import annotation.tailrec
import java.nio.ByteBuffer
import java.io._
import resource._
import java.nio.channels.{Channels, WritableByteChannel, ReadableByteChannel}

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
    streamCopy(Channels.newChannel(in), Channels.newChannel(out))
    in.close()
  }

  private def streamCopy(src: ReadableByteChannel, dest: WritableByteChannel) {
    val buffer = getByteBuffer(4 * 1024)
    @tailrec
    def loop() {
      if(src.read(buffer) > 0) {
        buffer.flip
        dest.write(buffer)
        buffer.compact
        loop
      }
    }
    loop

    buffer.flip
    while(buffer.hasRemaining) {
      dest.write(buffer)
    }
  }

  protected def getByteBuffer(size: Int) = ByteBuffer.allocate(size)
}

/**
 * Extension for types that exist as a File.  This is useful primarily for zero-copy optimizations.
 */
trait FileRenderable extends InputStreamRenderable {
  def file: File

  def in: InputStream = new FileInputStream(file)

  override protected def getByteBuffer(size: Int) = ByteBuffer.allocateDirect(size) // zero-copy byte buffer
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

  implicit def writesToWriterToCharRenderable(writesToWriter: { def writeTo(writer: Writer): Unit }) =
    new CharRenderable {
      def writeTo(out: OutputStream, cs: Charset) {
        for (writer <- managed(new OutputStreamWriter(out, cs))) {
          writeTo(writer)
        }
      }
      def writeTo(writer: Writer) { writesToWriter.writeTo(writer) }
    }

  implicit def writesToStreamToRenderable(writesToStream: { def writeTo(out: OutputStream): Unit }) =
    new Renderable {
      def writeTo(out: OutputStream, cs: Charset) { writesToStream.writeTo(out) }
    }
}