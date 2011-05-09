
import sbt._


class SJSProject(info: ProjectInfo) extends DefaultWebProject(info) {

  lazy val ossSonatypeRepo = MavenRepository("Sonatype OSS Repo", "http://oss.sonatype.org/content/repositories/releases")

  lazy val atmosphereModuleConfig = ModuleConfiguration("org.atmosphere", ossSonatypeRepo)

  lazy val scalaToolsRelRepo = MavenRepository("Scala Tools Releases Repo", "http://scala-tools.org/repo-releases")

  lazy val sjsonModuleConfig = ModuleConfiguration("net.debasishg", scalaToolsRelRepo)

  lazy val repo2MavenRepo = MavenRepository("Official Maven2 Repo", "http://repo2.maven.org/maven2")

  lazy val guiceMaven2RepoModuleConfig = ModuleConfiguration("com.google.inject", repo2MavenRepo)

  lazy val guiceExtMaven2RepoModuleConfig = ModuleConfiguration("com.google.inject.extensions", repo2MavenRepo)


  val servletApi = "org.mortbay.jetty" % "servlet-api" % "2.5-20081211" % "provided"

  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.v20100331" % "test" withSources()

  val sjson = "net.debasishg" % "sjson_2.8.1" % "0.9.1" withSources()

  val atmosphere_runtime = "org.atmosphere" % "atmosphere-runtime" % "0.7.1" withSources()
  val atmosphere_runtimejq = "org.atmosphere" % "atmosphere-jquery" % "0.7.1"

  val logback_classic = "ch.qos.logback" % "logback-classic" % "0.9.24"

  val logback_core = "ch.qos.logback" % "logback-core" % "0.9.24"

  val guice = "com.google.inject" % "guice" % "3.0" withSources()

  val guiceServlet = "com.google.inject.extensions" % "guice-servlet" % "3.0" withSources()

}

