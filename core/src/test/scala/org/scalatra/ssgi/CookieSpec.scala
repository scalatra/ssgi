package org.scalatra.ssgi

import org.scalatest.matchers.MustMatchers
import org.scalatest.{WordSpec}

class CookieSpec extends WordSpec with MustMatchers {

  "a Cookie" should {
    "render a simple name value pair" in {
      val cookie = Cookie("theName", "theValue")
      cookie.toCookieString must startWith("theName=theValue")
    }

    "render a simple name value pair with a version" in {
      val cookie = Cookie("theName", "theValue")
      cookie.toCookieString must startWith("theName=theValue; Version=1")
    }

    "have a dot in front of the domain when set" in {
      val cookie = Cookie("cookiename", "value1")( CookieOptions(domain="nowhere.com"))
      cookie.toCookieString must startWith("cookiename=value1; Domain=.nowhere.com")
    }

    "prefix a path with / if a path is set" in {
      val cookie = Cookie("cookiename", "value1")( CookieOptions(path="path/to/resource"))
      cookie.toCookieString must  startWith("cookiename=value1; Path=/path/to/resource")
    }

    "have a maxAge when the value is >= 0" in {
      val cookie = Cookie("cookiename", "value1")(CookieOptions(maxAge=86700))
      cookie.toCookieString must  startWith("cookiename=value1; Max-Age=86700")
    }

    "set the comment when a comment is given" in {
      val cookie = Cookie("cookiename", "value1")(CookieOptions(comment="This is the comment"))
      cookie.toCookieString must startWith("cookiename=value1; Comment=This is the comment")
    }

    "flag the cookie as secure if needed" in {
      val cookie = Cookie("cookiename", "value1")(CookieOptions(secure = true))
      cookie.toCookieString must startWith("cookiename=value1; Secure")
    }

    "flag the cookie as http only if needed" in {
      val cookie = Cookie("cookiename", "value1")( CookieOptions(httpOnly = true))
      cookie.toCookieString must startWith("cookiename=value1; HttpOnly")
    }

    "render a cookie with all options set" in {
      val cookie = Cookie("cookiename", "value3")(CookieOptions(
        domain="nowhere.com",
        path="path/to/page",
        comment="the cookie thingy comment",
        maxAge=15500,
        secure=true,
        httpOnly=true,
        version=654
      ))
      cookie.toCookieString must
       equal("cookiename=value3; Domain=.nowhere.com; Path=/path/to/page; Comment=the cookie thingy comment; " +
               "Max-Age=15500; Secure; HttpOnly; Version=654")
    }
  }
}