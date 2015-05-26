package org.denigma.gsea

import akka.actor._
import akka.http.scaladsl.Http.{ServerBinding, IncomingConnection}

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl._

import akka.routing._
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.Source
import HttpMethods._
import org.denigma.gsea.bioconductor.{StartRServe, BioActor}

import scala.concurrent.Future
import scala.util.{Success, Failure}

class MainActor  extends Actor with ActorLogging with Routes
{
  implicit val system = context.system
  implicit val materializer = ActorFlowMaterializer()
  implicit val executionContext = system.dispatcher

  val server: HttpExt = Http(context.system)
  var serverSource: Source[IncomingConnection, Future[ServerBinding]] = null

  override def receive: Receive = {
    case AppMessages.Start(port)=>
      val host = "localhost"
      server.bindAndHandle(routes, host, port)
      StartRServe.runLocalRserve.onComplete{
        case Success(res)=>     log.info(s"starting server at $host:$port")

        case Failure(th)=> this.log.error(s"R serve has not been started because of $th")

      }

    case AppMessages.Stop=> onStop()
  }

  def onStop() = {
    log.info("Main actor has been stoped...")
  }

  override def postStop() = {
    onStop()
  }

}
