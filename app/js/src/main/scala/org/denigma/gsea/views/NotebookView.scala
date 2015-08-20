package org.denigma.gsea.views

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.controls.login.Session
import org.denigma.gsea.storage.WebSocketStorage
import org.scalajs.dom
import org.scalajs.dom.raw._
import rx.Rx
import rx.core.Var
import rx.ops._

class NotebookView(val elem:HTMLElement,val session:Session,val params:Map[String,Any]) extends BindableView
{

  val channel = this.resolveKeyOption("channel"){
    case ch:String => ch
  }.getOrElse("notebook")

  val websocketOpt:Var[Option[WebSocket]] = Var(None)

  val connected = websocketOpt.map(_.isDefined)

  val addClick: Var[dom.MouseEvent] = Var(Events.createMouseEvent())
  addClick.handler{
    dom.console.log("CLICK WORKS!")
    websocketOpt.now match {
      case Some(w)=>
        w.send(s"HELLO! +${Math.random()*1000}")
      case _ => dom.console.error("websocket is not avaliable")
    }
  }

  val userChange = session.userChange
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
  val username: Rx[String] = session.username

  val onOpen = Var(Events.createEvent())

  val onMessage = Var( Events.createMessageEvent() )
  val onError = Var( Events.createErrorEvent() )
  val onClose = Var(Events.createEvent())

  onOpen.handler(
    dom.alert(s"I am opened")
  )
  onMessage.foreach(e=>
    if(e.data!=null) dom.alert("MESSAGE = "+e.data.toString)
  )

  val urlOpt:Var[Option[String]] = Var(None)


  def connect(name:String) = {
      val url = getWebsocketUri(name,channel)
      this.websocketOpt() = Some(WebSocketStorage.init(url){
      w=>
        w.onopen = { (event: Event) ⇒ onOpen() = event }
        w.onerror = { (event: ErrorEvent) ⇒ onError() = event }
        w.onmessage = { (event: MessageEvent) ⇒ onMessage() = event}
        w.onclose = { (event: Event) ⇒  onClose() = event}
        w
      })
  }

  def disconnect(name:String,channel:String = "notebook") = {
    val url = getWebsocketUri(name)
    WebSocketStorage.remove(url)
  }



  def getWebsocketUri(nameOfChatParticipant: String,channel:String = "notebook"): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/channel/$channel?username=${username.now}"
  }

}
