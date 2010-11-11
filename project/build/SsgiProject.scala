import sbt._

class SsgiProject(info: ProjectInfo) extends DefaultProject(info) {
  lazy val core = project("core", "ssgi-core", new CoreProject(_))
  class CoreProject(info: ProjectInfo) extends DefaultProject(info) with SsgiSubProject {
    val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"
    val commonsLang =  "commons-lang" % "commons-lang" % "2.5"
    val mimeUtil = "eu.medsea.mimeutil" % "mime-util" % "2.1.3"
    val scalaArm = "com.github.jsuereth.scala-arm" % "scala-arm_2.8.0" % "0.2"
  }

  lazy val servlet = project("servlet", "ssgi-servlet", new ServletProject(_), core)
  class ServletProject(info: ProjectInfo) extends DefaultProject(info) with SsgiSubProject {
    val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided" withSources
  }

  lazy val examples = project("examples", "ssgi-example", new Examples(_))
  class Examples(info: ProjectInfo) extends ParentProject(info) {
    val servletExample = project("servlet", "ssgi-servlet-example", new ServletExampleProject(_), servlet)
    class ServletExampleProject(info: ProjectInfo) extends DefaultWebProject(info) with SsgiSubProject {
      val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.25" % "test"
    }
  }

  trait SsgiSubProject {
    this: BasicScalaProject =>
    val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"
    val mockito = "org.mockito" % "mockito-core" % "1.8.5" % "test"
  }
}
