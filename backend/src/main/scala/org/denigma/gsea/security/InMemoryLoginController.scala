package org.denigma.gsea.security

import java.util.UUID
import javax.management.openmbean.KeyAlreadyExistsException

import org.denigma.gsea.domain.User
import org.denigma.gsea.utils.BiMap

import scala.concurrent.Future
import scala.util.{Success, Failure, Try}
import com.github.t3hnar.bcrypt._



class InMemoryLoginController extends LoginController {

  var users:Map[String,User] = Map.empty

  def register(username:String,passw:String):Try[String] = users.get(username) match {
    case Some(user)=>
      Failure(new KeyAlreadyExistsException(username))
    case None=>
      val hash = passw.bcrypt
      users = users + (username -> User(username, hash))
      Success(hash)
  }

  def findHash(username: String): Future[Option[String]]  = Future.successful(users.get(username).map(_.hash))

  def clean() =  users = BiMap.empty //for testing
}