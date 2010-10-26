package org.scalatra.ssgi

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

class HttpMethodSpec extends Spec with ShouldMatchers {
  describe("HttpMethod") {
    it("should resolve standard methods case insensitively") {
      HttpMethod("gET") should be (Get)
    }

    it("should return an extension method, uppercase, for non-standard methods methods") {
      HttpMethod("foo") should equal (ExtensionMethod("FOO"))
    }
  }
}