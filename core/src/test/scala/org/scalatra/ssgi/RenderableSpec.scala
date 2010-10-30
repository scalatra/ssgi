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
    "write itself to an output stream in the specified encoding" in {
      val s = "stríñg"
      renderViaStream(s, "utf-8") should equal (s.getBytes("utf-8"))
      renderViaStream(s, "iso-8859-1") should equal (s.getBytes("iso-8859-1"))
    }
  }

  "A node sequence" should {
    "write itself to an output stream in the specified encoding" in {
      val ns = <nødèSêq></nødèSêq>
      renderViaStream(ns, "utf-8") should equal (ns.toString.getBytes("utf-8"))
      renderViaStream(ns, "iso-8859-1") should equal (ns.toString.getBytes("iso-8859-1"))
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
    "write itself to an output stream" in {
      val bytes = "File".getBytes
      val file = File.createTempFile("ssgitest", "tmp")
      try {
        val fw = new FileWriter(file)
        try {
          fw.write("File");
          fw.flush();
        }
        finally {
          fw.close();
        }
        renderViaStream(file) should equal (bytes)
      }
      finally {
        file.delete();
      }
    }
  }
}