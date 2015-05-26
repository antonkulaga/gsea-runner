package org.denigma.gsea.templates
import scalacss.Defaults._
import scalacss.ScalatagsCss._
import scalatags.Text._
import scalatags.Text.all._

object Index {

  lazy val content = html(
    head(
      title := "Hello world!",
      link(rel := "stylesheet", href := "mystyles.css")
    ),
    body(
      h1("Hello World!"),
      div(
        p(`class`:="desc","This project will be used to build a plasmid bank!"),
      script(src:="resources/frontend-fastopt.js"),
      script(src:="resources/frontend-launcher.js")
      )
   )
  )
  lazy val template = content.render


}
/*
<html>
<body>
<div>Hello World!</div>

<form action="#">
    <label for="name">Name: </label><input id="name" type="text" />
    <input id="join" type="button" value="Join!"/>
</form>

<form action="#">
    <label for="message">Say something: </label><input id="message" type="text" />
    <input id="send" type="button" value="Send" disabled="true"/>
</form>

<div id="playground"/>

<script type="text/javascript" src="frontend-fastopt.js"></script>
<script type="text/javascript" src="frontend-launcher.js"></script>
</body>
</html>
 */
