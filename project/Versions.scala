
object Versions extends WebJarsVersions with RLangVersions with ScalaJsVersions with SharedVersions
{
  val scala = "2.11.6"

  val akkaHttp = "1.0-RC3"

  val ammonite = "0.3.0"

  val retry = "0.2.1"

  val bcrypt = "2.4"

  val apacheCodec = "1.10"

}

trait ScalaJsVersions {

  val jqueryFacade = "0.6"

  val dom ="0.8.1"

  val binding = "0.7.15"

}

//versions for libs that are shared between client and server
trait SharedVersions
{
  val autowire = "0.2.5"

  val scalaRx = "0.2.8"

  val quicklens = "1.3.1"

  val scalaTags = "0.5.1"

  val scalaCSS = "0.2.0"

  val productCollections = "1.4.2"

  val scalatest = "3.0.0-SNAP5"

}



trait RLangVersions {

  val REngine = "2.1.0"

  val RServe = "1.8.1"
}

trait WebJarsVersions{

  val jquery =  "2.1.3"

  val semanticUI = "1.12.3"

  val selectize = "0.12.1"

  val threeJS = "r66"

  val codemirror = "5.3"
}
