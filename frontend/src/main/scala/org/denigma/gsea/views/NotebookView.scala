package org.denigma.gsea.views

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.views.BindableView
import org.denigma.gsea.storage.WebSocketStorage
import org.scalajs.dom
import org.scalajs.dom.raw._
import rx.Rx
import rx.core.Var
import org.denigma.binding.extensions._
import rx.ops._




class NotebookView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
{

  val channel = this.resolveKeyOption("channel"){
    case ch:String => ch
  }.getOrElse("websocket")

  val userChange = Session.userChange
  userChange.foreach{
    case (None,None) => //do nothing
    case (Some(uname),None)=>
      this.disconnect(uname)
    case (Some(one),Some(two))=>
      this.disconnect(one)
      this.connect(two)
    case (None,Some(uname))=>
      this.connect(uname)
      //dom.alert((a,b).toString())
  }
  val username: Rx[String] = Session.username

  val onOpen = Var(EventBinding.createEvent())
  val onMessage = Var( dom.document.createEvent("MessageEvent").asInstanceOf[MessageEvent])
  val onError = Var( dom.document.createEvent("ErrorEvent").asInstanceOf[ErrorEvent])
  val onClose = Var(EventBinding.createEvent())

  onOpen.handler(
    dom.alert(s"I am opened")
  )

  val urlOpt:Var[Option[String]] = Var(None)


  def connect(name:String) = {
      val url = getWebsocketUri(name,channel)
      WebSocketStorage.init(url){
      w=>
        w.onopen = { (event: Event) ⇒ onOpen() = event }
        w.onerror = { (event: ErrorEvent) ⇒ onError() = event }
        w.onmessage = { (event: MessageEvent) ⇒ onMessage() = event}
        w.onclose = { (event: Event) ⇒  onClose() = event}
        w
      }
  }

  def disconnect(name:String,channel:String = "websocket") = {
    val url = getWebsocketUri(name)
    WebSocketStorage.remove(url)
  }


  val websocketOpt:Var[Option[WebSocket]] = Var(None)

  def getWebsocketUri(nameOfChatParticipant: String,channel:String = "websocket"): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/$channel?name=$nameOfChatParticipant"
  }

  override protected def attachBinders(): Unit =  withBinders( BindableView.defaultBinders(this) )

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

}
