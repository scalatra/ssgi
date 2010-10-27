package org.scalatra.ssgi.util

class RicherString(orig: String) {
  def isBlank = orig == null || orig.trim.isEmpty
  def isNonBlank = orig != null && !orig.trim.isEmpty
}
