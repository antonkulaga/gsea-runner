package org.denigma.gsea.views

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.raw._
import rx.core.Var
import org.denigma.binding.extensions._
import rx.ops._


class NotebookView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
{

  val username:Var[Option[String]] = Var(None)

  val joinClick = Var(EventBinding.createMouseEvent())
    joinClick.handler{
  }

  val onOpen = Var(EventBinding.createEvent())
  val onMessage = Var( dom.document.createEvent("MessageEvent").asInstanceOf[MessageEvent])
  val onError = Var( dom.document.createEvent("ErrorEvent").asInstanceOf[ErrorEvent])
  val onClose = Var(EventBinding.createEvent())


  def connect(username:String,channel:String = "websocket") = {
    val w = new WebSocket(getWebsocketUri(username))
    w.onopen = { (event: Event) ⇒ onOpen() = event }
    w.onerror = { (event: ErrorEvent) ⇒ onError() = event }
    w.onmessage = { (event: MessageEvent) ⇒ onMessage() = event}
    w.onclose = { (event: Event) ⇒  onClose() = event}
  }


  val websocketOpt:Var[Option[WebSocket]] = Var(None)

  def getWebsocketUri(nameOfChatParticipant: String,channel:String = "websocket"): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/$channel?name=$nameOfChatParticipant"
  }

  override protected def attachBinders(): Unit =  withBinders( BindableView.defaultBinders(this) )

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

}
