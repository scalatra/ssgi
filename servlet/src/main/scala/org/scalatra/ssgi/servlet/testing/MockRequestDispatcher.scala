package org.scalatra.ssgi.servlet.testing

import javax.servlet.{ServletRequest, ServletResponse, RequestDispatcher}

class MockRequestDispatcher extends RequestDispatcher {

  private var _path: String = "/"
  private var _forwardedRequest: ServletRequest = null
  private var _forwardedResponse: ServletResponse = null
  private var _includedRequest: ServletRequest = null
  private var _includedResponse: ServletResponse = null

  def setPath(path: String) = {
    _path = path
    this
  }

  def getPath(path: String) = _path

  def forward(request: ServletRequest, response: ServletResponse) = {
    _forwardedRequest = request
    _forwardedResponse = response
    this
  }

  def include(request: ServletRequest, response: ServletResponse) = {
    _includedRequest = request
    _includedResponse = response
    this
  }

  def getForwardedRequest = _forwardedRequest
  def getForwardedResponse = _forwardedResponse
  def getIncludedRequest = _includedRequest
  def getIncludedResponse = _includedResponse

}