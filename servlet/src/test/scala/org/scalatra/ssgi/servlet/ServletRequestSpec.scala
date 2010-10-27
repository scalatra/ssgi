package org.scalatra.ssgi
package servlet

import org.scalatest.{OneInstancePerTest, Spec}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import javax.servlet.http.HttpServletRequest
import scala.collection.JavaConversions._
import java.util.Enumeration
import javax.servlet.ServletInputStream

class ServletRequestSpec extends Spec with ShouldMatchers with MockitoSugar with OneInstancePerTest {
  val delegate = mock[HttpServletRequest]
  val req = new ServletRequest(delegate)

  describe("ServletRequest.requestMethod") {
    it("should derive from getMethod") {
      when(delegate.getMethod) thenReturn "GET"
      req.requestMethod should be (Get)
    }
  }

  describe("ServletRequest.scriptName") {
    it("should be the concatenation of contextPath and servletPath") {
      when(delegate.getContextPath) thenReturn "/contextPath"
      when(delegate.getServletPath) thenReturn "/servletPath"
      req.scriptName should equal ("/contextPath/servletPath")
    }
  }

  describe("ServletRequest.pathInfo") {
    describe("when getPathInfo is not null") {
      it("should equal getPathInfo") {
        when(delegate.getPathInfo) thenReturn "/pathInfo"
        req.pathInfo should equal ("/pathInfo")
      }
    }

    describe("when getPathInfo is null") {
      it("should be the empty string") {
        when(delegate.getPathInfo) thenReturn null
        req.pathInfo should equal ("")
      }
    }
  }

  describe("ServletRequest.queryString") {
    describe("when getQueryString is not null") {
      it("should equal getQueryString") {
        when(delegate.getQueryString) thenReturn "foo=bar"
        req.queryString should equal ("foo=bar")
      }
    }

    describe("when getQueryString is null") {
      it("should be the empty string") {
        when(delegate.getQueryString) thenReturn null
        req.queryString should equal ("")
      }
    }
  }

  describe("ServletRequest.contentType") {
    describe("when getContentType is not null") {
      it("should equal Some(getContentType)") {
        when(delegate.getContentType) thenReturn "application/octet-stream"
        req.contentType should equal (Some("application/octet-stream"))
      }
    }

    describe("when getContentType is null") {
      it("should be None") {
        when(delegate.getContentType) thenReturn null
        req.contentType should equal (None)
      }
    }
  }

  describe("ServletRequest.contentLength") {
    describe("when getContentLength is positive") {
      it("should equal Some(getContentLength)") {
        when(delegate.getContentLength) thenReturn 1024
        req.contentLength should equal (Some(1024))
      }
    }

    describe("when getContentLength is zero") {
      it("should equal Some(0)") {
        when(delegate.getContentLength) thenReturn 0
        req.contentLength should equal (Some(0))
      }
    }

    describe("when getContentType is negative") {
      it("should be None") {
        when(delegate.getContentLength) thenReturn -1
        req.contentLength should equal (None)
      }
    }
  }

  describe("ServletRequest.serverName") {
    it("should equal getServerName") {
      when(delegate.getServerName) thenReturn "www.scalatra.org"
      req.serverName should be ("www.scalatra.org")
    }
  }

  describe("ServletRequest.serverPort") {
    it("should equal getServerPort") {
      when(delegate.getServerPort) thenReturn 80
      req.serverPort should be (80)
    }
  }

  describe("ServletRequest.serverProtocol") {
    it("should equal getProtocol") {
      when(delegate.getProtocol) thenReturn "HTTP/1.1"
      req.serverProtocol should be ("HTTP/1.1")
    }
  }

  describe("ServletRequest.headers") {
    it("should iterate over all headers") {
      when(delegate.getHeaderNames.asInstanceOf[Enumeration[String]]) thenReturn Iterator("foo", "bar")
      when(delegate.getHeaders("foo").asInstanceOf[Enumeration[String]]) thenReturn Iterator("oof")
      when(delegate.getHeaders("bar").asInstanceOf[Enumeration[String]]) thenReturn Iterator("bar")
      req.headers.keys should equal (Set("foo", "bar"))
    }

    it("should return Some(Seq) for a known header") {
      when(delegate.getHeaders("Numbers").asInstanceOf[Enumeration[String]]) thenReturn Iterator("1", "2", "3")
      req.headers.get("Numbers") should equal (Some(List("1", "2", "3")))
    }

    it("should return None for an unknown header") {
      when(delegate.getHeaders("Unknown").asInstanceOf[Enumeration[String]]) thenReturn null
      req.headers.get("Unknown") should equal (None)
    }
  }

  describe("ServletRequest.scheme") {
    it("should equal getScheme") {
      when(delegate.getScheme) thenReturn "http"
      req.scheme should be ("http")
    }
  }

  describe("ServletRequest.inputStream") {
    it("should equal getInputStream") {
      val inputStream = mock[ServletInputStream]
      when(delegate.getInputStream) thenReturn inputStream
      req.inputStream should be (inputStream)
    }
  }

  describe("ServletRequest.attributes") {
    it("should iterate over all attributes") {
      when(delegate.getAttributeNames.asInstanceOf[Enumeration[String]]) thenReturn Iterator("foo", "bar")
      when(delegate.getAttribute("foo")).thenReturn(<oof />, Array[Object](): _*)
      when(delegate.getAttribute("bar")).thenReturn("rab", Array[Object](): _*)
      req.attributes should equal (Map("foo" -> <oof />, "bar" -> "rab"))
    }

    it("should return Some(val) for a known attribute") {
      when(delegate.getAttribute("foo")).thenReturn(<oof />, Array[Object](): _*)
      req.attributes.get("foo") should equal (Some(<oof />))
    }

    it("should return None for an unknown header") {
      when(delegate.getHeaders("Unknown")) thenReturn null
      req.attributes.get("Unknown") should equal (None)
    }

    it("should setAttribute on update") {
      req.attributes("ssgi") = "rocks"
      verify(delegate).setAttribute("ssgi", "rocks")
    }

    it("should removeAttribute on remove") {
      req.attributes.remove("servlet dependency")
      verify(delegate).removeAttribute("servlet dependency")
    }
  }
}
