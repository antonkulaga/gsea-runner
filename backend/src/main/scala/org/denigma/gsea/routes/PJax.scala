package org.denigma.gsea.routes

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.util.Tupler
import akka.http.scaladsl.server.{Directive, Directive0, Directives}
import play.twirl.api.Html

import scala.concurrent.Future


/*trait PJaxMagnet {
  type Out
  def directive: Directive[Out]
}*/

trait PJaxMagnet {
  def directive: Directive[Tuple1[Html]]
}

object PJaxMagnet {

  implicit def apply(params:(Html,Html=>Html)):PJaxMagnet =
  new PJaxMagnet {
    def directive = Directive[Tuple1[Html]] { inner ⇒ ctx ⇒
        val (html, transform) = params
        if (ctx.request.headers.exists(h => h.lowercaseName() == "x-pjax"))
          inner(Tuple1(html))(ctx)
        else
          inner(Tuple1(transform(html)))(ctx)
      }
  }

}


trait PJax {

  def pjax(magnet: PJaxMagnet):Directive[Tuple1[Html]] = magnet.directive
}
