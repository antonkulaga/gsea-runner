import com.typesafe.sbt.gzip.Import.gzip
import com.typesafe.sbt.web.SbtWeb.autoImport._
import com.typesafe.sbt.web.pipeline.Pipeline
import com.typesafe.sbt.web.{PathMapping, SbtWeb}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import playscalajs.PlayScalaJS
import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin._

lazy val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug

lazy val publishSettings = Seq(
  bintrayRepository := "denigma-releases",

  bintrayOrganization := Some("denigma"),

  licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0")),

  bintrayPublishIvyStyle := true
)

/**
 * For parts of the project that we will not publish
 */
lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)


//settings for all the projects
lazy val commonSettings = Seq(
  scalaVersion := Versions.scala,
  organization := "org.denigma",
  resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"), //for scala-js-binding
  libraryDependencies ++= Dependencies.commonShared.value++Dependencies.testing.value,
  updateOptions := updateOptions.value.withCachedResolution(true) //to speed up dependency resolution
)

val scalaJSDevStage  = Def.taskKey[Pipeline.Stage]("Apply fastOptJS on all Scala.js projects")

def scalaJSDevTaskStage: Def.Initialize[Task[Pipeline.Stage]] = Def.task { mappings: Seq[PathMapping] =>
  mappings ++ PlayScalaJS.devFiles(Compile).value ++ PlayScalaJS.sourcemapScalaFiles(fastOptJS).value
}


lazy val root = Project("root",file("."),settings = commonSettings)
  .settings(
    mainClass in Compile := (mainClass in appJVM in Compile).value,
    (managedClasspath in Runtime) += (packageBin in appJVM in Assets).value
  ) dependsOn appJVM aggregate(appJVM, appJS)


lazy val app = crossProject
  .crossType(CrossType.Full)
  .in(file("app"))
  .settings(commonSettings++publishSettings: _*)
  .settings(
    name := "gsea",
    version := Versions.gsea
  )
  .jsSettings(
    libraryDependencies ++= Dependencies.sjsLibs.value,
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    jsDependencies += RuntimeDOM % "test"
  )
  .jvmSettings(Revolver.settings:_*)
  .jvmSettings(
    libraryDependencies ++= Dependencies.akka.value ++ Dependencies.webjars.value,
    mainClass in Compile :=Some("org.denigma.gsea.Main"),
    mainClass in Revolver.reStart := Some("org.denigma.gsea.Main"),
    libraryDependencies ++= Dependencies.RLang.value ++ Dependencies.compilers.value ++ Dependencies.otherJvm.value,
    scalaJSDevStage := scalaJSDevTaskStage.value,
    //pipelineStages := Seq(scalaJSProd,gzip),
    (emitSourceMaps in fullOptJS) := true,
    pipelineStages in Assets := Seq(scalaJSDevStage,gzip), //for run configuration
    (fullClasspath in Runtime) += (packageBin in Assets).value //to package production deps
  )
  .jvmConfigure(p=>p.enablePlugins(SbtTwirl,SbtWeb))

lazy val appJVM = app.jvm
lazy val appJS = app.js

