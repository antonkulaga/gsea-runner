package org.denigma.gsea.views

import java.util.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.stage._
//import org.denigma.gsea.chat.{Chat, ChatMessage}
import akka.http.scaladsl.server.Directives._
import scala.concurrent.duration._

trait WebService
{

/*  def webSocket = path("websocket"~Slash) {
    parameter('name) { name ⇒
      handleWebsocketMessages(websocketChatFlow(sender = name))
    }
  }*/

/*
  def websocketChatFlow(sender: String): Flow[Message, Message, Unit] =
    Flow[Message]
      .collect {
        case TextMessage.Strict(msg) ⇒ msg // unpack incoming WS text messages...
        // This will lose (ignore) messages not received in one chunk (which is
        // unlikely because chat messages are small) but absolutely possible
        // FIXME: We need to handle TextMessage.Streamed as well.
      }
      .via(theChat.chatFlow(sender)) // ... and route them through the chatFlow ...
      .map {
        case ChatMessage(sender, message) ⇒ TextMessage.Strict(s"$sender: $message") // ... pack outgoing messages into WS text messages ...
      }
      .via(reportErrorsFlow) // ... then log any processing errors on stdin

  def reportErrorsFlow[T]: Flow[T, T, Unit] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
        def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

        override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
          println(s"WS stream failed with $cause")
          super.onUpstreamFailure(cause, ctx)
        }
      })*/
}
