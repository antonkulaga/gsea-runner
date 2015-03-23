import NativePackagerKeys._
import com.typesafe.sbt.SbtNativePackager.packageArchetype
import com.typesafe.sbt.web
import web.SbtWeb.autoImport
import web.Import.WebKeys._

name := "gsea-runner"

version := "0.1.0"

scalaVersion := "2.11.6"

bintraySettings

resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases")

//resolvers += sbt.Resolver.bintrayRepo("inthenow", "releases")

val akkaVersion = "2.3.9"

val akkaHttpVersion = "1.0-M4"

val scalaTagsVersion =  "0.4.6"

val zcheckVersion = "0.6.1"

val scalaCheckVersion = "1.12.2"

//val rJavaVersion = "0.9-7"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion

libraryDependencies += "com.lihaoyi" %% "scalatags" % scalaTagsVersion

libraryDependencies += "com.typesafe.akka" %% "akka-kernel" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

//libraryDependencies += "com.github.inthenow" %% "zcheck" % zcheckVersion

//libraryDependencies += "com.github.inthenow" %% "scalacheck" % scalaCheckVersion % "test"


mainClass in Compile := Some("org.denigma.gsea.MainKernel")

managedResourceDirectories in Compile += (webModuleDirectory in Assets).value

managedResourceDirectories in Test += (webModuleDirectory in TestAssets).value

WebKeys.packagePrefix in Assets := "public/"

(managedClasspath in Runtime) += (packageBin in Assets).value


//testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck)

lazy val runner = (project in file("."))
  .enablePlugins(SbtWeb)
  .enablePlugins(AkkaAppPackaging)
