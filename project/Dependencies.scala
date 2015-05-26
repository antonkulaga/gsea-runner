import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

	lazy val testing = Def.setting(Seq(
		"com.lihaoyi" %%% "utest" % Versions.utest % "test"
	))

	lazy val akka = Def.setting(Seq(
		"com.typesafe.akka" %% "akka-stream-experimental" % Versions.akkaHttp,

		"com.typesafe.akka" %% "akka-http-core-experimental" % Versions.akkaHttp,

		"com.typesafe.akka" %% "akka-http-experimental" % Versions.akkaHttp,

		"com.typesafe.akka" %% "akka-http-testkit-experimental" % Versions.akkaHttp
	))

	lazy val templates = Def.setting(Seq(
		"com.github.japgolly.scalacss" %%% "core" % Versions.scalaCSS,

		"com.github.japgolly.scalacss" %%% "ext-scalatags" %  Versions.scalaCSS
	))

	lazy val sjsLibs= Def.setting(Seq(
		"org.scala-js" %%% "scalajs-dom" % Versions.dom,

		"org.querki" %%% "jquery-facade" % Versions.jqueryFacade,

		"org.querki" %%% "querki-jsext" % Versions.jsext,

		"org.denigma" %%% "binding" % Versions.binding

	))

	lazy val webjars= Def.setting(Seq(
		"org.webjars" % "jquery" % Versions.jquery,

		"org.webjars" % "Semantic-UI" % Versions.semanticUI,

		"org.webjars" % "selectize.js" % Versions.selectize


	))

	val RLang = Def.setting(Seq(
		"org.rosuda.REngine" % "REngine" % Versions.REngine,

		"org.rosuda.REngine" % "Rserve" % Versions.RServe
	))

	val otherJvm = Def.setting(Seq(
		"me.lessis" %% "retry" % Versions.retry
	))


	val compilers = Def.setting(Seq(
		"org.scala-lang" % "scala-compiler" % Versions.scala
	))


}
