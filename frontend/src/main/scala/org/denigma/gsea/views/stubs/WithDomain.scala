package org.denigma.gsea.views.stubs

import org.scalajs.dom
import rx.core.Var

trait WithDomain {

  val domain = Var(dom.window.location.host)

}
