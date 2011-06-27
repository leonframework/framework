
import sbt._


class LeonParentProject(info: ProjectInfo) extends ParentProject(info) with UnpublishedProject {

  // ===================================================================================================================
  // Repositories
  // ===================================================================================================================

  lazy val ossSonatypeRepo = MavenRepository("Sonatype OSS Repo", "http://oss.sonatype.org/content/repositories/releases")

  lazy val atmosphereModuleConfig = ModuleConfiguration("org.atmosphere", ossSonatypeRepo)

  lazy val scalaToolsRelRepo = MavenRepository("Scala Tools Releases Repo", "http://scala-tools.org/repo-releases")

  lazy val sjsonModuleConfig = ModuleConfiguration("net.debasishg", scalaToolsRelRepo)

  lazy val repo2MavenRepo = MavenRepository("Official Maven2 Repo", "http://repo2.maven.org/maven2")

  lazy val guiceMaven2RepoModuleConfig = ModuleConfiguration("com.google.inject", repo2MavenRepo)

  lazy val guiceExtMaven2RepoModuleConfig = ModuleConfiguration("com.google.inject.extensions", repo2MavenRepo)

  // ===================================================================================================================
  // Dependencies for subprojects: Intentionally defs!
  // ===================================================================================================================

  val specs2 = "org.specs2" %% "specs2" % "1.3" % "test" withSources()

  def servletApi = "org.mortbay.jetty" % "servlet-api" % "2.5-20081211" % "provided"

  def jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.v20100331" % "test" withSources()

  def rhino = "rhino" % "js" % "1.7R2" withSources()

  def sjson = "net.debasishg" % "sjson_2.8.1" % "0.9.1" withSources()

  def freemarker = "org.freemarker" % "freemarker" % "2.3.18" withSources()

  def atmosphere_version = "0.7.1"

  def atmosphere_runtime = "org.atmosphere" % "atmosphere-runtime" % atmosphere_version withSources()
  def atmosphere_runtimejq = "org.atmosphere" % "atmosphere-jquery" % atmosphere_version

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

  def javassist = "org.javassist" % "javassist" % "3.14.0-GA" withSources()


  // ===================================================================================================================
  // Publishing
  // ===================================================================================================================

  override def managedStyle = ManagedStyle.Maven
  // override def deliverAction = super.deliverAction dependsOn(publishLocal) // Fix for issue 99!
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
  // lazy val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  // lazy val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
  val publishTo = Resolver.file("Local Test Repository", Path fileProperty "java.io.tmpdir" asFile)


  // ===================================================================================================================
  // Defaults for sub projects
  // ===================================================================================================================

  trait LeonDefaults extends LicenseHeaders {
    
    def licenseText =
      """Copyright (c) 2011 WeigleWilczek and others.

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
"""

    override def compileAction = super.compileAction dependsOn formatLicenseHeaders

  }

  // ===================================================================================================================
  // core subproject
  // ===================================================================================================================

  val leon_core = project("leon-core", "leon-core", new LeonCoreProject(_))

  class LeonCoreProject(info: ProjectInfo) extends DefaultProject(info) with LeonDefaults {

    def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")

    override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

    override def libraryDependencies =
      Set(specs2, logback_classic, logback_core, servletApi, freemarker, rhino,
        atmosphere_runtime, atmosphere_runtimejq, guice, guiceServlet, sjson, snakeYaml,
        casbah_core, javassist, h2database)

    override def packageSrcJar = defaultJarPath("-sources.jar")
    lazy val sourceArtifact = sources(artifactID) // lazy is important here!
    override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

    //override def testClasspath = super.testClasspath --- providedClasspath // Needed because of crippled javaee-(web-)api!
  }

  // ===================================================================================================================
  // Sample subprojects
  // ===================================================================================================================

  val leon_samples_project = project("leon-samples", "leon-samples", new LeonSamplesProject(_))

  class LeonSamplesProject(info: ProjectInfo) extends ParentProject(info) {
    val leon_samples_mixed_project = project("leon-samples-mixed", "leon-samples-mixed", new LeonSamplesMixedProject(_), leon_core)
  }

  class LeonSamplesMixedProject(info: ProjectInfo) extends DefaultWebProject(info) with UnpublishedProject with LeonDefaults {

    override def libraryDependencies = Set(jetty7, mysql)

    override def defaultExcludes =
      super.defaultExcludes || "*-sources.jar" || "atmosphere-ping-" + atmosphere_version + ".jar"

  }
}

trait UnpublishedProject extends BasicManagedProject {
   override def publishLocalAction = task { None }
   override def deliverLocalAction = task { None }
   override def publishAction = task { None }
   override def deliverAction = task { None }
   override def artifacts = Set.empty
}


