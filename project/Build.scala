import sbt._
import sbt.Project.Initialize
import Keys._
import com.github.siasia._
import com.banno.license.Plugin._
import LicenseKeys._
import WebPlugin._

object BuildSettings {
  val buildOrganization = "io.leon"
  val buildVersion      = "0.4.2-SNAPSHOT"
  val buildScalaVersion = "2.9.1"
  val buildDescription  = "JVM web framework for building data-driven web applications"

  val licenseText =
"""Copyright (c) 2011 WeigleWilczek and others.

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
"""

  val buildSettings =
    Defaults.defaultSettings ++
    licenseSettings ++
    Seq(scalacOptions ++= Seq("-unchecked", "-Xfatal-warnings", "-deprecation")) ++
    Seq(javacOptions ++= Seq("-source", "1.6", "-target", "1.6")) ++
    Seq(
      organization := buildOrganization,
      description  := buildDescription,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      license      := licenseText,
      licenses     := Seq("Eclipse Public License - v 1.0" -> url("http://www.eclipse.org/legal/epl-v10.html")),
      homepage     := Some(url("http://leon.io")),
      crossPaths   := false) ++      
    (testFrameworks += new TestFramework("de.johoop.testng.TestNGFramework")) ++
    (testOptions <+= (crossTarget, resourceDirectory in Test) map { (target, testResources) =>
      Tests.Argument(
        "-d",
        (target / "testng").absolutePath,
        (testResources / "testng.xml").absolutePath
      )
    })

  def leonArtifactName =
    (config: String, module: ModuleID, artifact: Artifact) =>
        module.name + "-" + module.revision + "." + artifact.extension

}

object Publish {

  val developers =
    ("blank", "Daniela Blank") ::
    ("blankenhorn", "Jan Blankenhorn") ::
    ("burgmer", "Philipp Burgmer") ::
    ("hohenbichler", "Johannes Hohenbichler") ::
    ("mricog", "Marco Rico Gomez") ::
    ("romanroe", "Roman Roelofsen") ::
    ("thurow", "Alexander Thurow") :: Nil

  val publishSettings = Seq(
    pomExtra := leonPomExtra,
    pomIncludeRepository := { _ => false }, // omit <repositories/> section.
    organizationName := "Leon Framework",
    organizationHomepage := Some(url("http://leon.io")),
    publishTo <<= sonatype,
//    publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository"))),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )

  def leonPomExtra = {
    <inceptionYear>2011</inceptionYear>
    <scm>
      <url>git@github.com:leonframework/framework.git</url>
      <connection>scm:git:git@github.com:leonframework/framework.git</connection>
    </scm>
    <developers>
      {
        developers sortBy { _._1 } map { case (id, name) =>
          <developer>
            <id>{ id }</id>
            <name>{ name }</name>
          </developer>
        }
      }
    </developers>
  }

  def sonatype: Initialize[Option[Resolver]] = {
    (version) { version: String =>
      val nexus = "https://oss.sonatype.org/"
        if (version.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    }
  }
}

object Dependencies {

  def scalatest = "org.scalatest" %% "scalatest" % "1.7.1" % "test" withSources()

  def testng = "org.testng" % "testng" % "6.1.1" % "test" withSources()

  def sbt_testng_interface = "de.johoop" % "sbt-testng-interface" % "1.0.0" % "test"


  def selenium = "org.seleniumhq.selenium" % "selenium-java" % "2.16.1" % "provided"

  def servletApi = "org.mortbay.jetty" % "servlet-api" % "2.5-20081211" % "provided"

  def jettyRuntime = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.v20100331" % "container" withSources()

  def jetty = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.v20100331" % "provided" withSources()

  def rhino = "org.mozilla" % "rhino" % "1.7R3" withSources()

  def freemarker = "org.freemarker" % "freemarker" % "2.3.18" withSources()

  def atmosphere_version = "0.7.1"

  def atmosphere_runtime = "org.atmosphere" % "atmosphere-runtime" % atmosphere_version withSources()

  def logback_classic = "ch.qos.logback" % "logback-classic" % "0.9.24"

  def logback_core = "ch.qos.logback" % "logback-core" % "0.9.24"

  //def slf4jLog4j(scope: String) = "org.slf4j" % "slf4j-log4j12" % "1.6.1" % scope

  def guice = "com.google.inject" % "guice" % "3.0" withSources()

  def guava = "com.google.guava" % "guava" % "11.0.1" withSources()

  def guiceServlet = "com.google.inject.extensions" % "guice-servlet" % "3.0" withSources()

  def mysql = "mysql" % "mysql-connector-java" % "5.1.16"

  def h2database = "com.h2database" % "h2" % "1.3.155" % "test" withSources()

  def snakeYaml = "org.yaml" % "snakeyaml" % "1.8" withSources()

  def mongo = "org.mongodb" % "mongo-java-driver" % "2.7.2" withSources()

  def jerichoHtml = "net.htmlparser.jericho" % "jericho-html" % "3.2" withSources()

  def gson = "com.google.code.gson" % "gson" % "1.7.1" withSources()

  // Apache HBase
  // currently added as unmanaged dependencies
  //def hadoop = "org.apache.hadoop" % "hadoop-core" % "0.20.append-r1056497" withSources()
  //def hbase = "org.apache.hbase" % "hbase" % "0.92.0-20111220.024317-8" withSources()

}

object LeonBuild extends Build {
  import BuildSettings._
  import Publish._
  import Dependencies._

  resolvers ++= Seq(
    "Sonatype OSS Repo" at "http://oss.sonatype.org/content/repositories/releases",
    "Official Maven2 Repo" at "http://repo2.maven.org/maven2",
    "Apache Release" at "https://repository.apache.org/content/repositories/releases",
    "Apache Public" at "https://repository.apache.org/content/repositories/public",
    "Apache Snapshots" at "https://repository.apache.org/content/repositories/snapshots")

  val coreDeps = Seq(
    testng,
    scalatest,
    sbt_testng_interface,
    jetty,
    selenium,
    logback_classic,
    logback_core,
    servletApi,
    freemarker,
    rhino,
    atmosphere_runtime,
    guice,
    guava,
    guiceServlet,
    snakeYaml,
    mongo,
    jerichoHtml,
    gson,
    h2database)

  val samplesDeps = Seq(servletApi, jettyRuntime, jetty, selenium)

  lazy val leon: Project = Project(
    "leon",
    file("."),
    settings = buildSettings) aggregate(
      leon_core,
      leon_hbase,
      leon_suite,
      leon_dummyapp
      //leon_samples_demos_addressbook_coffee_coffee
      //samplesAjaxReverserJavaJs,
      //samplesAjaxReverserJsJs,
      //samplesAjaxReverserWithPojoJavaJs,
      //samplesCometPingJavaCoffee
      //samplesMixed,
      //samplesLeonJax,
      //samplesDeviceOrientation
    )

  lazy val leon_core = Project(
    "leon-core",
    file("leon-core"),
    settings = buildSettings ++ publishSettings ++
      Seq(libraryDependencies ++= coreDeps))

  lazy val leon_hbase = Project(
    "leon-hbase",
    file("leon-hbase"),
    settings = buildSettings ++ publishSettings ++
      Seq(libraryDependencies ++= (/* hadoop +: hbase +: */ coreDeps))
  ) dependsOn(leon_core)

  lazy val leon_dummyapp = Project(
    "leon-dummyapp",
    file("leon-dummyapp"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(leon_core)

  lazy val leon_suite = Project(
    "leon-suite",
    file("leon-suite"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(leon_core)

  lazy val leon_samples_demos_addressbook_coffee_coffee = Project(
    "leon-samples-demos-addressbook-coffee_coffee",
    file("leon-samples/demos/addressbook/coffee_coffee"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(leon_core)

  // ------------------------------------------------------
  // Examples
  // ------------------------------------------------------

  /*

  lazy val samplesAjaxReverserJavaJs = Project(
    "samplesAjaxReverserJavaJs",
    file("leon-samples/ajax/reverser/java_js"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(leon_core)

  lazy val samplesAjaxReverserJsJs = Project(
    "samplesAjaxReverserJsJs",
    file("leon-samples/ajax/reverser/js_js"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(leon_core)


  lazy val samplesAjaxReverserWithPojoJavaJs = Project(
    "samplesAjaxReverserWithPojoJavaJs",
    file("leon-samples/ajax/reverser-with-pojo/java_js"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(leon_core)

  lazy val samplesCometPingJavaCoffee = Project(
    "samplesCometPingJavaCoffee",
    file("leon-samples/comet/ping/java_coffee"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(leon_core)
  */

  /*
  lazy val samplesMixed = Project(
    "leon-samples-mixed",
    file("leon-samples/leon-samples-mixed"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
    ) dependsOn(core)
  */

  /*
  lazy val samplesLeonJax = Project(
    "leonjax",
    file("leon-samples/leonjax"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
    ) dependsOn(core)
  */

  /*
  lazy val samplesDeviceOrientation = Project(
    "deviceorientation",
    file("leon-samples/deviceorientation"),
    settings = buildSettings ++ webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(core)
  */

}
