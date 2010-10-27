package org.scalatra.ssgi

import java.util.Locale
import java.net.URLEncoder

case class CookieOptions(
        domain  : String  = "",
        path    : String  = "",
        maxAge  : Int     = -1,
        secure  : Boolean = false,
        comment : String  = "",
        httpOnly: Boolean = false,
        encoding: String  = "UTF-8",
        version: Int = 1)

case class Cookie(name: String, value: String)(implicit cookieOptions: CookieOptions = CookieOptions()) {

  import Implicits._

  def toCookieString = {
    val encoding = if(cookieOptions.encoding.isNonBlank) cookieOptions.encoding else "UTF-8"
    val sb = new StringBuffer
    sb append URLEncoder.encode(name, encoding) append "="
    sb append URLEncoder.encode(value, encoding)

    if(cookieOptions.domain.isNonBlank) sb.append("; Domain=").append(
      (if (!cookieOptions.domain.startsWith(".")) "." + cookieOptions.domain else cookieOptions.domain).toLowerCase(Locale.ENGLISH)
    )

    val pth = cookieOptions.path
    if(pth.isNonBlank) sb append "; Path=" append (if(!pth.startsWith("/")) {
      "/" + pth
    } else { pth })

    if(cookieOptions.comment.isNonBlank) sb append ("; Comment=") append cookieOptions.comment

    if(cookieOptions.maxAge > -1) sb append "; Max-Age=" append cookieOptions.maxAge

    if (cookieOptions.secure) sb append "; Secure"
    if (cookieOptions.httpOnly) sb append "; HttpOnly"
    if (cookieOptions.version > 0) sb append "; Version=" append cookieOptions.version
    sb.toString
  }
}