
object Versions extends WebJarsVersions with RLangVersions with ScalaJsVersions with SharedVersions
{
  val scala = "2.11.6"

  val akkaHttp = "1.0-RC3"

  val ammonite = "0.3.0"

  val retry = "0.2.1"

}

trait ScalaJsVersions {

  val jqueryFacade = "0.5"

  val jsext = "0.5"

  val dom = "0.8.0"

  val binding = "0.7.12"

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

  val utest = "0.3.1"
}



trait RLangVersions {

  val REngine = "2.1.0"

  val RServe = "1.8.1"
}

trait WebJarsVersions{

  val jquery =  "2.1.3"

  val semanticUI = "1.12.3"

  val selectize = "0.12.0"

  val threeJS = "r66"

  val codemirror = "4.11"
}
