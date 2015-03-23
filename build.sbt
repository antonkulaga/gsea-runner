import NativePackagerKeys._
import com.typesafe.sbt.SbtNativePackager.packageArchetype
import com.typesafe.sbt.web.SbtWeb.autoImport


name := "gsea-runner"

version := "0.1.0"

scalaVersion := "2.11.6"

bintraySettings

resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases")

val akkaVersion = "2.3.9"

val akkaHttpVersion = "1.0-M4"

val scalaTagsVersion =  "0.4.6"

//val rJavaVersion = "0.9-7"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion

libraryDependencies += "com.lihaoyi" %% "scalatags" % scalaTagsVersion

libraryDependencies += "com.typesafe.akka" %% "akka-kernel" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

mainClass in Compile := Some("org.denigma.gsea.MainKernel")

(managedClasspath in Runtime) += (packageBin in Assets).value

lazy val runner = (project in file("."))
  .enablePlugins(SbtWeb)
  .enablePlugins(AkkaAppPackaging)