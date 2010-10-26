import sbt._

class SsgiProject(info: ProjectInfo) extends DefaultProject(info) {
  lazy val core = project("core", "core", new CoreProject(_))
  class CoreProject(info: ProjectInfo) extends DefaultProject(info) with SsgiSubProject {
    val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  }

  lazy val servlet = project("servlet", "servlet", new ServletProject(_), core)
  class ServletProject(info: ProjectInfo) extends DefaultProject(info) with SsgiSubProject {
    val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  }

  trait SsgiSubProject {
    this: BasicScalaProject =>
    val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"
  }
}
