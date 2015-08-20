package org.denigma.gsea

import org.denigma.binding.binders.{NavigationBinding, GeneralBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.controls.login.{AjaxSession, LoginView}
import org.denigma.gsea.views._
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.denigma.binding.extensions._
import org.semantic.SidebarConfig
import org.semantic.ui._
import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.Try

/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name = "main"

  override val params: Map[String, Any] = Map.empty

  lazy val elem: HTMLElement = dom.document.body

  val sidebarargs = SidebarConfig.exclusive(false).dimPage(false).closable(false).useLegacy(false)

  val session = new AjaxSession()

  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("menu")( (el, args) =>new MenuView(el,args))
    .register("sidebar")( (el, args) =>new SidebarView(el,args))
    .register("login")( (el, args) =>new LoginView(el,session,args))
    .register("notebook")( (el, args) =>new NotebookView(el,session,args))
    .register("code")( (el, args) =>new CodeView(el,args))
    .register("code-cell")( (el, args) =>new CodeCellView(el,args))

  this.binders = List(new GeneralBinder(this),new NavigationBinding(this))

  @JSExport
  def main(): Unit = {
    this.bindView(this.viewElement)

    Example.activate() //activate examples
  }

  @JSExport
  def showLeftSidebar() = {
    $(".left.sidebar").sidebar(sidebarargs).show()
  }

  @JSExport
  def load(content: String, into: String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from: String, into: String): Unit = {
    for {
      ins <- sq.byId(from)
      intoElement <- sq.byId(into)
    } {
      this.loadElementInto(intoElement, ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }
  }

}
