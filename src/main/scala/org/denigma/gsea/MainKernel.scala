package org.denigma.gsea

import akka.actor.ActorSystem
import akka.http.Http.IncomingConnection
import akka.http.model._
import HttpMethods._
import akka.http.Http
import akka.kernel.Bootable
import akka.stream.ActorFlowMaterializer
import akka.actor._
import akka.routing.{DefaultResizer, SmallestMailboxPool, BalancingPool, RoundRobinRouter}
import scala.concurrent.duration._

import scala.concurrent.Future

/**
 * For running as kernel
 */
object MainKernel extends Bootable with App
{
  implicit val system = ActorSystem()

  var main:ActorRef = null

  println(getClass.getResource("/public/hello.txt"))



  override def startup(): Unit = {

    main =  system.actorOf(Props[Main])
    main ! AppMessages.Start(1234)

  }

  override def shutdown(): Unit = {
    system.shutdown()
  }

  startup()


}