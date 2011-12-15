import sbt._
import Keys._
import com.github.siasia._
import com.banno.license.Plugin._
import LicenseKeys._
import WebPlugin._

object BuildSettings {
  val buildOrganization = "io.leon"
  val buildVersion      = "0.2.0"
  val buildScalaVersion = "2.9.1"

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
    Seq(scalacOptions ++= Seq("-unchecked", "-Xfatal-warnings")) ++
    Seq(
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      license      := licenseText,

      // workaround for sbt issue #206 (remove 'watchTransitiveSources' when sbt 0.11.1 is released)
      // https://github.com/harrah/xsbt/issues/206
      watchTransitiveSources <<=
        Defaults.inDependencies[Task[Seq[File]]](
          watchSources.task, const(std.TaskExtra.constant(Nil)), aggregate = true, includeRoot = true) apply {
            _.join.map(_.flatten)
        }
    )

  val publishSettings = Seq(
    publishTo := Some(Resolver.file("Local Test Repository", Path fileProperty "java.io.tmpdir" asFile))
    //,
    //credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
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

  def jerichoHtml = "net.htmlparser.jericho" % "jericho-html" % "3.2" withSources()
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
    jerichoHtml,
    h2database)

  val samplesDeps = Seq(servletApi, jetty7)


  lazy val leon = Project(
    "leon",
    file("."),
    settings = buildSettings
    ) aggregate(core, samplesMixed, samplesLeonJax, samplesDeviceOrientation)

  lazy val core = Project(
    "leon-core",
    file("leon-core"),
    settings = buildSettings ++ publishSettings ++
      Seq(libraryDependencies ++= coreDeps)
  )

  lazy val samplesMixed = Project(
    "leon-samples-mixed",
    file("leon-samples/leon-samples-mixed"),
    settings = buildSettings ++
      webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
    ) dependsOn(core)

  lazy val samplesLeonJax = Project(
    "leonjax",
    file("leon-samples/leonjax"),
    settings = buildSettings ++
      webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
    ) dependsOn(core)

  lazy val samplesDeviceOrientation = Project(
    "deviceorientation",
    file("leon-samples/deviceorientation"),
    settings = buildSettings ++
      webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(core)

  lazy val samplesCometPing = Project(
    "cometping",
    file("leon-samples/cometping"),
    settings = buildSettings ++
      webSettings ++
      Seq(libraryDependencies ++= samplesDeps)
  ) dependsOn(core)

}
