import sbt._
import Keys._
import com.github.siasia._
import WebappPlugin.webappSettings

object BuildSettings {
  val buildOrganization = "io.leon"
  val buildVersion      = "0.0.1"
  val buildScalaVersion = "2.9.1"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )

  val publishSettings = Seq(
    publishTo := Some(Resolver.file("Local Test Repository", Path fileProperty "java.io.tmpdir" asFile)),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )
}

object Dependencies {
  val specs2 = "org.specs2" %% "specs2" % "1.6.1" % "test" withSources()

  def servletApi = "org.mortbay.jetty" % "servlet-api" % "2.5-20081211" % "provided"

  def jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.v20100331" % "container" withSources()

  def rhino = "rhino" % "js" % "1.7R2" withSources()

  def sjson = "net.debasishg" %% "sjson" % "0.15" withSources()

  def freemarker = "org.freemarker" % "freemarker" % "2.3.18" withSources()

  def atmosphere_version = "0.7.1"

  def atmosphere_runtime = "org.atmosphere" % "atmosphere-runtime" % atmosphere_version withSources()

  def logback_classic = "ch.qos.logback" % "logback-classic" % "0.9.24"

  def logback_core = "ch.qos.logback" % "logback-core" % "0.9.24"

  //def slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.2" withSources

  //def slf4jLog4j(scope: String) = "org.slf4j" % "slf4j-log4j12" % "1.6.1" % scope

  def guice = "com.google.inject" % "guice" % "3.0" withSources()

  def guiceServlet = "com.google.inject.extensions" % "guice-servlet" % "3.0" withSources()

  def mysql = "mysql" % "mysql-connector-java" % "5.1.16"

  def h2database = "com.h2database" % "h2" % "1.3.155" % "test" withSources()

  def snakeYaml = "org.yaml" % "snakeyaml" % "1.8" withSources()

  def casbah_core = "com.mongodb.casbah" %% "casbah-core" % "2.1.5-1" withSources()

  def commonsBeanutils = "commons-beanutils" % "commons-beanutils" % "1.8.3" withSources()

  def commonsCollections = "commons-collections" % "commons-collections" % "3.2.1" withSources()
}

object LeonBuild extends Build {
  import BuildSettings._
  import Dependencies._

  resolvers ++= Seq(
    "Sonatype OSS Repo" at "http://oss.sonatype.org/content/repositories/releases",
    "Scala Tools Releases Repo" at "http://scala-tools.org/repo-releases",
    "Official Maven2 Repo" at "http://repo2.maven.org/maven2")

  val coreDeps = Seq(
    specs2,
    logback_classic,
    logback_core,
    servletApi,
    freemarker,
    rhino,
    atmosphere_runtime,
    guice,
    guiceServlet,
    sjson,
    snakeYaml,
    casbah_core,
    commonsBeanutils,
    commonsCollections,
    h2database)

  val samplesDeps = Seq(servletApi)


  lazy val leon = Project(
    "leon",
    file("."),
    settings = buildSettings ++
      Seq(libraryDependencies += jetty7) ++
      container.deploy("/" -> samplesMixed)
  ) aggregate(core,  samplesMixed)

  lazy val core = Project(
    "leon-core",
    file("leon-core"),
    settings = buildSettings ++ publishSettings ++
      Seq(libraryDependencies ++= coreDeps)
  )

  lazy val samplesMixed = Project(
    "leon-samples-mixed",
    file("leon-samples/leon-samples-mixed"),
    settings = buildSettings ++ webappSettings ++
      Seq(libraryDependencies ++= samplesDeps)
    ) dependsOn(core)


  lazy val container = Container("container")
}