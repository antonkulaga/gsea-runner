import com.typesafe.sbt.SbtNativePackager
import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.web.SbtWeb.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import play.twirl.sbt._
import play.twirl.sbt.SbtTwirl.autoImport._
import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin._


object Build extends sbt.Build {

	//settings for all the projects
	lazy val commonSettings = Seq(
		scalaVersion := Versions.scala,
		organization := "org.denigma",
		testFrameworks += new TestFramework("utest.runner.Framework"),
		resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"),
		libraryDependencies ++= Dependencies.testing.value
	)

	//sbt-native-packager settings to run it as daemon
	lazy val packageSettings = Seq(
		maintainer := "Anton Kulaga <antonkulaga@gmail.com>",
		packageSummary:= "Runner for gene set enrichment analysis",
		packageDescription := "Runner for gene set enrichement analysis"
		//serverLoading in Debian := Upstart
	)

	// code shared between backend and frontend
	lazy val shared = crossProject
		.crossType(CrossType.Pure)
		.in(file("shared"))
		.settings(commonSettings: _*)
		.settings(name := "shared")
	lazy val sharedJVM = shared.jvm
	lazy val sharedJS = shared.js

	// Scala-Js frontend
	lazy val frontend = Project("frontend", file("frontend"))
		.settings(commonSettings: _*)
		.settings(
			persistLauncher in Compile := false,
			persistLauncher in Test := false,
			jsDependencies += RuntimeDOM % "test",
			testFrameworks += new TestFramework("utest.runner.Framework"),
			libraryDependencies ++= Dependencies.sjsLibs.value++Dependencies.templates.value
		) enablePlugins ScalaJSPlugin dependsOn sharedJS

	//backend project
	lazy val backend = Project("backend", file("backend"),settings = commonSettings++Revolver.settings)
		.settings(packageSettings:_*)
		.settings(
			libraryDependencies ++= Dependencies.akka.value
				++Dependencies.templates.value
				++Dependencies.webjars.value
				++Dependencies.RLang.value
				++Dependencies.compilers.value
				++Dependencies.otherJvm.value
			,
			mainClass in Compile :=Some("org.denigma.gsea.Main"),
			mainClass in Revolver.reStart := Some("org.denigma.gsea.Main"),
			resourceGenerators in Compile <+=  (fastOptJS in Compile in frontend,
				packageScalaJSLauncher in Compile in frontend) map( (f1, f2) => Seq(f1.data, f2.data)),
			watchSources <++= (watchSources in frontend),
			(managedClasspath in Runtime) += (packageBin in Assets).value
		).enablePlugins(SbtTwirl,SbtWeb) dependsOn sharedJVM

	lazy val root = Project("root",file("."),settings = commonSettings)
		.settings(
			mainClass in Compile := (mainClass in backend in Compile).value,
			libraryDependencies += "com.lihaoyi" % "ammonite-repl" % Versions.ammonite cross CrossVersion.full,
   		initialCommands in console := """ammonite.repl.Repl.run("")"""
		) dependsOn backend aggregate(backend,frontend)
}