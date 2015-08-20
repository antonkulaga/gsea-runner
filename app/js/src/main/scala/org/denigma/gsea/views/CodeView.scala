package org.denigma.gsea.views

import org.denigma.binding.views.BindableView
import org.denigma.binding.views.collections.CollectionView
import org.denigma.codemirror.Doc
import org.denigma.controls.binders.CodeBinder
import org.scalajs.dom.raw.{HTMLElement, HTMLTextAreaElement}
import rx.ops._
import rx.{Rx, Var}

import scala.collection.immutable.Map
import scala.scalajs.js

class CodeView(val elem:HTMLElement,val params:Map[String,Any]) extends CollectionView
{
  override type Item = Var[CodeCell]

  override type ItemView = CodeCellView

  override def newItem(item: Item): CodeCellView = this.constructItemView(item,Map[String,Any]("cell"->item)){case (el,mp)=>
    new CodeCellView(el,mp)
  }

  override val items: Var[List[Item]] = Var(List(Var(CodeCell.empty)))

}

class CodeCellView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
{
  lazy val cell: Var[CodeCell] = this.resolveKey("cell"){
    case cellVar:Var[CodeCell]=>cellVar
    case code:CodeCell=>Var(code)
  }

  lazy val code: Rx[String] = cell.map(c=>c.code)
  lazy val result: Rx[String] = cell.map(c=>c.result)

  lazy val hasResult = result.map(r=>r!="")


}


class CodeCellBinder(view:BindableView,onCtrlEnter:Doc=>Unit) extends CodeBinder(view) {
  lazy val ctrlHandler: js.Function1[Doc, Unit] = onCtrlEnter
  //lazy val delHandler:js.Function1[Doc,Unit] = onDel


  override def makeEditor(area: HTMLTextAreaElement, textValue: String, codeMode: String, readOnly: Boolean = false) = {
    val editor = super.makeEditor(area, textValue, codeMode, readOnly)
    val dic = js.Dictionary(
      "Ctrl-Enter" -> ctrlHandler
    )
    editor.setOption("extraKeys", dic)
    editor
  }

}
