package org.scalatra.ssgi

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import java.nio.charset.Charset
import java.io._

class RenderableSpec extends WordSpec with ShouldMatchers {
  private def renderViaStream(r: Renderable, cs: Charset = "utf-8") = {
    val out = new ByteArrayOutputStream
    r.writeTo(out, cs)
    out.toByteArray
  }

  private def renderViaWriter(r: CharRenderable) = {
    val w = new StringWriter
    r.writeTo(w)
    w.toString
  }

  private implicit def string2Charset(s: String): Charset = Charset.forName(s)

  "A traversable of bytes" should {
    "write itself to an output stream" in {
      val array: Array[Byte] = "traversable of bytes".getBytes
      renderViaStream(array.toIterable) should equal (array)
    }
  }

  "An array of bytes" should {
    "write itself to an output stream" in {
      val array: Array[Byte] = "array of bytes".getBytes
      renderViaStream(array) should equal (array)
    }
  }

  "A string of bytes" should {
    val s = "stríñg"

    "write itself to an output stream in the specified encoding" in {
      renderViaStream(s, "utf-8") should equal (s.getBytes("utf-8"))
      renderViaStream(s, "iso-8859-1") should equal (s.getBytes("iso-8859-1"))
    }

    "write itself to a writer" in {
      renderViaWriter(s) should equal (s)
    }
  }

  "A node sequence" should {
    val ns = <nødèSêq></nødèSêq>

    "write itself to an output stream in the specified encoding" in {
      renderViaStream(ns, "utf-8") should equal (ns.toString.getBytes("utf-8"))
      renderViaStream(ns, "iso-8859-1") should equal (ns.toString.getBytes("iso-8859-1"))
    }

    "write itself to a writer" in {
      renderViaWriter(ns) should equal (ns.toString)
    }
  }

  "An input stream" should {
    "write itself to an output stream" in {
      val bytes = new Array[Byte](5000)
      for (i <- 0 until bytes.size) { bytes(i) = ((i % 127) + 1).toByte }
      val in = new ByteArrayInputStream(bytes)
      renderViaStream(in) should equal (bytes)
    }

    "be closed after rendering" in {
      val in = new ByteArrayInputStream(Array[Byte]()) {
        var closed = false
        override def close() = { super.close; closed = true }
      }
      renderViaStream(in)
      in.closed should be (true)
    }
  }

  "A file" should {
    def withTempFile(content: Array[Byte])(f: File => Unit) {
      val file = File.createTempFile("ssgitest", "tmp")
      try {
        val fos = new FileOutputStream(file)
        try {
          fos.write(content);
          fos.flush();
        }
        finally {
          fos.close();
        }
        f(file)
      }
      finally {
        file.delete();
      }
    }

    "write itself to an output stream" in {
      val bytes = "File".getBytes
      withTempFile(bytes) { file =>
        renderViaStream(file) should equal (bytes)
      }
    }

    "return itself as a file" in {
      val bytes = "File".getBytes
      withTempFile(bytes) { file =>
        val renderable: FileRenderable = file
        renderable.file should equal (file)
      }
    }
  }

  "Something that can write to a writer" should {
    val string = "foo!"
    class WritesToWriter {
      def writeTo(writer: Writer) = {
        writer.write(string)
      }
    }

    "implicitly render itself to an output stream" in {
      renderViaStream(new WritesToWriter) should equal (string.getBytes)
    }

    "implicitly render itself to a writer" in {
      renderViaWriter(new WritesToWriter) should equal (string)
    }
  }
}