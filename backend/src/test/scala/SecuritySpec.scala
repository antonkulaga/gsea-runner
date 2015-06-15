import javax.management.openmbean.KeyAlreadyExistsException

import akka.http.scaladsl.model.headers.{HttpCookie, `Set-Cookie`}
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.t3hnar.bcrypt._
import org.denigma.gsea.routes.Registration
import org.denigma.gsea.security._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util._

class SecuritySpec  extends WordSpec with Matchers with Directives with ScalaFutures with  ScalatestRouteTest
{
  //val routes = new Router

  var users: Map[String,String] = Map.empty
  val loginController= new InMemoryLoginController
  val sessionController = new InMemorySessionController

  object logins extends Registration(loginController.checkPassword,sessionController.withToken)
  import loginController._

  val timeout = 500 millis
  implicit override val patienceConfig = new PatienceConfig(timeout)

  "authorization" should {
    "encode passwords with bcrypt" in {
      val (a,s,x) = (register("anton","pass1") ,  register("sasha","pass2") ,  register("xenia","pass3"))
      checkPassword("anton","pass2").futureValue shouldEqual false
      checkPassword("anton","pass1").futureValue shouldEqual true
      checkPassword("sasha","pass2").futureValue shouldEqual true
      checkPassword("sasha","pass3").futureValue shouldEqual false
      checkPassword("karmen","pass3").futureValue shouldEqual false
      loginController.clean()
    }

    "be able to encode/decode with AES" in {
      val (one,two,three) = ("one","two","three")
      val key = "hello encryption!!!"
      val wrongKey = "I am wrong"

      val (oneEn,twoEn,threeEn) = (AES.encrypt(one,key),AES.encrypt(two,key),AES.encrypt(three,key))
      AES.decrypt(oneEn,key) shouldEqual one
      AES.decrypt(twoEn,key) shouldEqual two
      AES.decrypt(threeEn,key) shouldEqual three
      assert(AES.decrypt(threeEn,key) != one)
      assert(AES.decrypt(threeEn,wrongKey) != three)
    }


    "generate cookies on login" in {
      loginController.register("anton","test")
      Get("/users/login?username=anton&password=test") ~> logins.routes ~> check{
        responseAs[String] shouldEqual "The user anton was logged in"
        val tokOpt = sessionController.getToken("anton")
        tokOpt.isDefined shouldEqual true
        val tok = tokOpt.get
        val hop: Option[`Set-Cookie`] = header[`Set-Cookie`]
        hop.isDefined shouldEqual(true)
        val h = hop.get.cookie
        h.name shouldEqual  "token"
        h.content shouldEqual tok
        loginController.clean()
      }
    }



  }

}