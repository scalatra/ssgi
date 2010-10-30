package org.scalatra.ssgi.servlet

import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar
import org.scalatest.{OneInstancePerTest, WordSpec}
import org.mockito.Mockito._
import org.mockito.Matchers._
import java.util.Locale
import javax.servlet.http.{Cookie, HttpServletResponse}

class SsgiServletResponseSpec extends WordSpec with MustMatchers with MockitoSugar with OneInstancePerTest  {

  val mockResponse = mock[HttpServletResponse]
  when(mockResponse.encodeURL(anyString)) thenReturn "encoded"

  val resp = new SsgiServletResponse(mockResponse)
  val actual = resp.ssgiResponse

  "A SsgiServletResponse" when {

    "the response is empty" should {

      "have a status of 200" in {
        actual.status must be (200)
      }

      "contain the Content-Length header" in {
        actual.headers must not be ('empty)
        actual.headers.get("Content-Length") must be (Some("0"))
      }

      "contain the Content-Type header" in {
        actual.headers.get("Content-Type") must be (Some(SsgiServletResponse.DEFAULT_CONTENT_TYPE + "; charset=" + SsgiServletResponse.DEFAULT_ENCODING))
      }

      "contain the Content-Language header" in {
        actual.headers.get("Content-Language") must be ('defined)
      }

      "contain the Date header" in {
        actual.headers.get("Date") must be ('defined)
      }

    }

    "setting some properties" should {

      "the buffersize must be int.maxvalue" in {
        resp.getBufferSize must be (Int.MaxValue)
      }

      "setting the buffersize makes no difference" in {
        resp.setBufferSize(543)
        resp.getBufferSize must be(Int.MaxValue)
      }

      "getting the character encoding when none is set should return the default" in {
        resp.getCharacterEncoding must be (SsgiServletResponse.DEFAULT_ENCODING)
      }

      "setting the character encoding should return the correct encoding" in {
        resp.setCharacterEncoding("NEW-ENCODING")
        resp.getCharacterEncoding must be ("NEW-ENCODING")
      }

      "set the content length" in {
        resp.setContentLength(12345)
        resp.ssgiResponse.headers.get("Content-Length") must be (Some("12345"))
      }

      "set the content type" in {
        resp.setContentType("text/html")
        resp.getContentType must be ("text/html")
      }

      "set the locale" in {
        resp.setLocale(Locale.US)
        resp.ssgiResponse.headers.get("Content-Language") must be (Some("en-us"))
      }

      "get the locale" in {
        resp.setLocale(Locale.US)
        resp.getLocale must be (Locale.US)
      }

      "isCommitted must always be false" in {
        resp.isCommitted must be (false)
      }
    }

    "resetting" should {

      "the buffer only should clear the body of content" in {
        resp.getWriter.println("Make body non-empty")
        resp.resetBuffer
        resp.getOutputStream.size must be (0)
      }

      "the entire response must reset the headers and the status to 200" in {
        resp.getWriter.println("Make body non-empty")
        resp.setStatus(400)
        resp.reset
        resp.ssgiResponse.status must be (200)
        resp.getOutputStream.size must be (0)
      }

    }

    "working with headers" should {

      "setting the status should change the status code" in {
        resp.setStatus(404)
        resp.ssgiResponse.status must be (404)
      }

      "setting an int header should replace an existing int header" in {
        resp.setIntHeader("theHeader", 4)
        resp.ssgiResponse.headers.get("theHeader") must be (Some("4"))
        resp.setIntHeader("theHeader", 7)
        resp.ssgiResponse.headers.get("theHeader") must be (Some("7"))
      }

      "adding an int header should not replace an existing int header" in {
        resp.addIntHeader("theHeader", 4)
        resp.ssgiResponse.headers.get("theHeader") must be (Some("4"))
        resp.addIntHeader("theHeader", 7)
        resp.ssgiResponse.headers.get("theHeader") must be (Some("4,7"))
      }

      "adding a cookie should serialize the servlet cookie to a header" in {
        resp.addCookie(new Cookie("theCookie", "the value"))
        resp.ssgiResponse.headers.get("Set-Cookie") must be (Some("theCookie=the+value"))
      }
    }

    "writing content" should {

      "to the output stream should produce a byte array" in {
        val bytes = "helloWorld".getBytes("UTF-8")
        resp.getOutputStream.write(bytes)
        resp.ssgiResponse.body.asInstanceOf[Array[Byte]] must be (bytes)
      }

      "to the print writer should produce a byte array" in {
        val bytes = "helloWorld".getBytes("UTF-8")
        resp.getWriter.print("helloWorld")
        resp.ssgiResponse.body.asInstanceOf[Array[Byte]] must be (bytes)
      }
    }

    "sending an error" should {
      "throw an exception if the error code < 400" in {
        evaluating { resp.sendError(200) } must produce [Exception]
      }
      "set the status correctly if the error code > 400" in {
        resp.sendError(404)
        resp.ssgiResponse.status must be (404)
      }
    }

    "redirecting" should {

      "encode the url" in {
        resp.sendRedirect("/go/somewhere")
        val ssgi = resp.ssgiResponse
        ssgi.headers.get("Location") must be (Some("encoded"))
        ssgi.status must be (302)
      }

    }
  }
}