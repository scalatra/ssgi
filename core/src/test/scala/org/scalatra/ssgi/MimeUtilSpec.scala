package org.scalatra.ssgi

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec

class MimeUtilSpec extends WordSpec with MustMatchers {

  "The mime util" when {

    "the file doesn't exist" must {

      "detect the correct mime type when an extension is present" in {
        val the_file = "helloworld.jpg"
        MimeUtil.mimeType(the_file) must equal("image/jpeg")
      }

      "fallback to the default value when no extension is present" in {
        val the_file = "helloworld"
        MimeUtil.mimeType(the_file) must equal(MimeUtil.DEFAULT_MIME)
      }
    }
  }
}