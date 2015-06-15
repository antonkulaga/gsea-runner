package org.denigma.gsea.security
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.util.Tupler
import akka.http.scaladsl.util.FastFuture._

import scala.concurrent.Future
import scala.util.{Try, _}


trait AuthDirectives {

  def withSession(magnet: SessionMagnet): Directive[Tuple1[String]] = magnet.directive

  def withRegistration(magnet: RegisterMagnet):Directive0 = magnet.directive

}



case class UserAlreadyExists(name:String) extends Rejection

case class ReadErrorRejection(message:String,exception:Throwable) extends Rejection








