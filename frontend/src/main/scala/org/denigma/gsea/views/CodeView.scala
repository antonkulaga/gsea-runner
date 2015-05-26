package org.denigma.gsea.views

import org.denigma.binding.views.BindableView
import org.denigma.binding.views.collections.CollectionView
import org.denigma.controls.binders.CodeBinder
import org.scalajs.dom.raw.HTMLElement
import rx.ops._
import rx.{Rx, Var}

class CodeView(val elem:HTMLElement,val params:Map[String,Any]) extends CollectionView
{
  override type Item = Var[CodeCell]

  override type ItemView = CodeCellView


  override def newItem(item: Item): CodeCellView = this.constructItem(item,Map[String,Any]("cell"->item)){case (el,mp)=>
    new CodeCellView(el,mp)
  }

  override val items: Rx[List[Item]] = Var(List.empty)

  override protected def attachBinders(): Unit =  withBinders( BindableView.defaultBinders(this) )

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

}

class CodeCellView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
{
  lazy val cell: Var[CodeCell] = this.resolveKey("cell"){
    case cellVar:Var[CodeCell]=>cellVar
    case code:CodeCell=>Var(code)
  }

  lazy val code = cell.map(c=>c.code)
  lazy val result = cell.map(c=>c.result)

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit =  withBinders( new CodeBinder(this)::BindableView.defaultBinders(this) )

}