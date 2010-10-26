package org.scalatra.ssgi

import eu.medsea.util.EncodingGuesser
import eu.medsea.mimeutil.{MimeType, MimeUtil2}
import collection.JavaConversions._

/**
 * A utility to help with mime type detection for a given file path or url
 */
object MimeUtil extends Logging {

  val DEFAULT_MIME = "application/octet-stream"

  private val mimeUtil = new MimeUtil2()
  quiet { mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector") }
  quiet { mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector") }
  registerEncodingsIfNotSet

  /**
   * Sets supported encodings for the mime-util library if they have not been
   * set. Since the supported encodings is stored as a static Set we
   * synchronize access.
   */
  private def registerEncodingsIfNotSet: Unit = synchronized {
    if (EncodingGuesser.getSupportedEncodings.size == 0) {
      val enc = Set("UTF-8", "ISO-8859-1", "windows-1252", EncodingGuesser.getDefaultEncoding)
      EncodingGuesser.setSupportedEncodings(enc)
    }
  }

  /**
   * Detects the mime type of a given file path.
   *
   * @param path The path for which to detect the mime type
   * @param fallback A fallback value in case no mime type can be found
   */
  def mimeType(path: String, fallback: String = DEFAULT_MIME) = {
    try {
      MimeUtil2.getMostSpecificMimeType(mimeUtil.getMimeTypes(path, new MimeType(fallback))).toString
    } catch {
      case e => {
        log.error("There was an error detecting the mime type", e)
        fallback
      }
    }
  }


  private def quiet(fn: => Unit) = {
    try { fn }
    catch { case e => log.error("An error occurred while registering a mime type detector", e) }
  }
}