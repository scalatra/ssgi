package org.scalatra.ssgi

import scala.xml.NodeSeq
import java.io.{File, InputStream}

trait Renderable

object Renderable {
  implicit def byteTraversableToRenderable(bytes: Traversable[Byte]) = new Renderable {}
  implicit def byteArrayToRenderable(bytes: Array[Byte]) = new Renderable {}
  implicit def stringToRenderable(string: String) = new Renderable {}
  implicit def nodeSeqToRenderable(nodeSeq: NodeSeq) = new Renderable {}
  implicit def inputStreamToRenderable(in: InputStream) = new Renderable {}
  implicit def fileToRenderable(file: File) = new Renderable {}
}