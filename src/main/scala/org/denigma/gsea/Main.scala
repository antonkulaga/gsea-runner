package org.denigma.gsea

import akka.actor._
import akka.http.Http.{IncomingConnection, ServerBinding}
import akka.http.model.HttpMethods._
import akka.http.model._
import akka.http.server.Route
import akka.http.server.RouteConcatenation._
import akka.http.model.HttpResponse
import akka.http.server.Directives._

import akka.http.{Http, HttpExt}
import akka.routing.{DefaultResizer, SmallestMailboxPool}
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.Future

class Main  extends Actor with ActorLogging{
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


  val route =
    path("order" / IntNumber) { id =>
      (get | put) { ctx =>
        ctx.complete("Received " + ctx.request.method.name + " request for order " + id)
      }
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
      serverSource = server.bind(interface = host, port)
      serverSource.runForeach { connection =>   connection.handleWithAsyncHandler(futureHandler)   }
      log.info(s"starting server at $host:$port")


    case AppMessages.Stop=>
      log.info("stopping")

  }

}
