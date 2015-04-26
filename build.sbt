import com.typesafe.sbt.packager.archetypes.ServerLoader.Upstart
import com.typesafe.sbt.web.Import.WebKeys._
import com.typesafe.sbt.packager.archetypes.TemplateWriter

name := "gsea-runner"

version := "0.1.1"

scalaVersion := "2.11.6"

bintraySettings

val akkaVersion = "2.3.9"

val akkaHttpVersion = "1.0-RC1"

val scalaTagsVersion =  "0.5.1"

val zcheckVersion = "0.6.1"

val scalaCheckVersion = "1.12.2"

resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases")

libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-scala-experimental" % akkaHttpVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit-scala-experimental" % akkaHttpVersion

libraryDependencies += "com.lihaoyi" %% "scalatags" % scalaTagsVersion

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

mainClass in Compile := Some("org.denigma.gsea.Main")

managedResourceDirectories in Compile += (webModuleDirectory in Assets).value

managedResourceDirectories in Test += (webModuleDirectory in TestAssets).value

WebKeys.packagePrefix in Assets := "public/"

(managedClasspath in Runtime) += (packageBin in Assets).value

lazy val runner = (project in file("."))
  .enablePlugins(SbtWeb)
  .enablePlugins(JavaServerAppPackaging)

maintainer in Linux := "Anton Kulaga <antonkulaga@gmail.com>"

packageSummary in Linux := "Runner for gene set enrichment analysis"

packageDescription := "Runner for gene set enrichement analysis"

serverLoading in Debian := Upstart