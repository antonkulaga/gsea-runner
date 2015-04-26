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

import scala.concurrent.Future

class MainActor  extends Actor with ActorLogging
{
  implicit val system = context.system
  implicit val materializer = ActorFlowMaterializer()
  implicit val executionContext = system.dispatcher

  val server: HttpExt = Http(context.system)
  var serverSource: Source[IncomingConnection, Future[ServerBinding]] = null

  val minWorkers = 2
  val defWorkersNumber = 5
  val maxWorkers = 10

  val resizer = DefaultResizer(lowerBound = minWorkers, upperBound = maxWorkers)
  val workers = context.actorOf(
    Props[BioActor].withRouter(SmallestMailboxPool(defWorkersNumber).withResizer(resizer)),
    name = "workerRouter")


  val routes: Route =      {
      //val path = "lib/gsea-runner/t"
      //val path = "webjars/gsea-runner/0.1.1/"
      val file = "hello.html"
      get {   getFromResource(file)     }
    }


  lazy val futureHandler: HttpRequest => Future[HttpResponse] = {


    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>


      val resp = HttpResponse(
        entity = HttpEntity(MediaTypes.`text/html`,
          "<html><body><h1>Hello world!</h1></body></html>"))
      Future.successful(resp)

    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>

      val resp = HttpResponse(
        entity = HttpEntity(MediaTypes.`text/html`,
          "<html><body><h1>Hello world!</h1></body></html>"))
      Future.successful(resp)

    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) => Future.failed(sys.error("BOOM!"))

    case _: HttpRequest                                => Future.successful(HttpResponse(404, entity = "Unknown resource!"))
  }

  override def receive: Receive = {
    case AppMessages.Start(port)=>
      val host = "localhost"
      server.bindAndHandle(routes, host, port)

      //serverSource = server.bind(interface = host, port)
      //serverSource.runForeach {      connection =>   connection.handleWithAsyncHandler(futureHandler)      }
      log.info(s"starting server at $host:$port")


    case AppMessages.Stop=>
      log.info("stopping")

  }

}