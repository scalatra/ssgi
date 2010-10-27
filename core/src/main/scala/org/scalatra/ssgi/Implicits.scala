package org.scalatra.ssgi

import util.RicherString

object Implicits {
  implicit def string2RicherString(orig: String) = new RicherString(orig)
}