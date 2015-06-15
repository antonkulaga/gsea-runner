package org.denigma.gsea.routes

import akka.http.scaladsl.server.Directives
import org.denigma.gsea.security._


class Router extends Directives {
  val sessionController:SessionController = new InMemorySessionController
  val loginController:LoginController = new InMemoryLoginController

  def routes = new Head().routes ~
    new Registration(
      loginController.checkPassword,
      sessionController.withToken)
      .routes ~
    new Pages().routes

}
