package org.denigma.gsea

import akka.actor.Actor
import akka.actor.Actor.Receive
import java.util.Enumeration

import org.rosuda.REngine.Rserve.RConnection

import scala.util.Try


class BioActor extends Actor with akka.actor.ActorLogging{

  var con: RConnection = null

  def tryClose() =     Try(if(con!=null){
    con.close()
    this.log.info("closing connection to RServe")
  })

  override def preStart(): Unit =       {
    con = new RConnection()
    val version = con.eval("R.version.string").asString()
    this.log.info(s"STARTING connection $version")
  }

  override def postStop() = tryClose()

  override def receive: Receive = {

    case RMessages.Code(str)=>
      sender ! RMessages.Result(con.eval(str))

    case some =>
      this.log.error(s"unknown message $some")
  }
}
