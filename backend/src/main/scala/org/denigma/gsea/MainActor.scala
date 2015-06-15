package org.denigma.gsea

import akka.actor._
import akka.http.scaladsl.Http.{IncomingConnection, ServerBinding}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.{Http, _}
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.Source
import org.denigma.gsea.bioconductor.StartRServe
import org.denigma.gsea.routes.Router
import org.denigma.gsea.security.UserAlreadyExists

import scala.concurrent.Future
import scala.util.{Failure, Success}

class MainActor  extends Actor with ActorLogging // Routes
{
  implicit val system = context.system
  implicit val materializer = ActorFlowMaterializer()
  implicit val executionContext = system.dispatcher


  val server: HttpExt = Http(context.system)
  var serverSource: Source[IncomingConnection, Future[ServerBinding]] = null
  val router = new Router()

  implicit def rejectionHandlers =
    RejectionHandler.newBuilder()
      .handle { case MissingCookieRejection(cookieName) =>
      complete(HttpResponse(BadRequest, entity = "No cookies, no service!!!"))
    }
      .handle { case AuthorizationFailedRejection ⇒
      complete(Forbidden, "You’re out of your depth!")
    }
      .handleAll[MethodRejection] { methodRejections ⇒
      val names = methodRejections.map(_.supported.name)
      complete(MethodNotAllowed, s"Can’t do that! Supported: ${names mkString " or "}!")
    }
      .handleAll[UserAlreadyExists]{ rjs=>
          complete("user no found exception")
      }
      .result()



  override def receive: Receive = {
    case AppMessages.Start(config)=>
      val (host,port) = (config.getString("app.host") , config.getInt("app.port"))
      server.bindAndHandle(router.routes, host, port)
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
