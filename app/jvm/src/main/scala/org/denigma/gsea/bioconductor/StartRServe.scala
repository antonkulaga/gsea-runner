package org.denigma.gsea.bioconductor
import java.io._

import org.rosuda.REngine.Rserve.RConnection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global


/** helper class that consumes output of a process. In addition, it filter output of the REG command on Windows to look for InstallPath registry entry which specifies the location of R. */
class StreamHog extends Thread {
  private[bioconductor] var is: InputStream = null
  private[bioconductor] var capture: Boolean = false
  private[bioconductor] var installPath: String = null

  private[bioconductor] def this(is: InputStream, capture: Boolean) {
    this()
    this.is = is
    this.capture = capture
    start()
  }

  def getInstallPath: String = {
    installPath
  }

  override def run() {
    try {
      val br: BufferedReader = new BufferedReader(new InputStreamReader(is))
      var line: String = null
      while ( {
        line = br.readLine
        line
      } != null) {
        if (capture) {
          val i: Int = line.indexOf("InstallPath")
          if (i >= 0) {
            var s: String = line.substring(i + 11).trim
            val j: Int = s.indexOf("REG_SZ")
            if (j >= 0) s = s.substring(j + 6).trim
            installPath = s
            System.out.println("R InstallPath = " + s)
          }
        }
        else System.out.println("Rserve>" + line)
      }
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }
}

/** simple class that start Rserve locally if it's not running already - see mainly <code>checkLocalRserve</code> method. It spits out quite some debugging outout of the console, so feel free to modify it for your application if desired.<p>
 <i>Important:</i> All applications should shutdown every Rserve that they started!
Never leave Rserve running if you started it after your application quits since it may pose a security risk.
Inform the user if you started an Rserve instance.
  */
object StartRServe { //THE CODE IS UGLY BECAUSE IT IS PORTED FROM JAVA

  implicit def timer  = odelay.jdk.JdkTimer.newTimer


  /** shortcut to <code>launchRserve(cmd, "--no-save --slave", "--no-save --slave", false)</code> */
  def launchRserve(cmd: String): Try[Unit] = launchRserve(cmd, "--no-save --slave", "--no-save --slave", debug = false)


  protected def isWindows = System.getProperty("os.name") match {
    case null=> false
    case osname if osname.length >= 7 && (osname.substring(0, 7) == "Windows")=> true
    case other => false
  }

  protected def getProcess(windows:Boolean,cmd: String, rargs: String, rsrvargs: String, debug: Boolean) = windows match {
    case true=> Runtime.getRuntime.exec("\"" + cmd + "\" -e \"library(Rserve);Rserve(" + (if (debug) "TRUE" else "FALSE") + ",args='" + rsrvargs + "')\" " + rargs)

    case false=> Runtime.getRuntime.exec(Array[String]("/bin/sh", "-c", "echo 'library(Rserve);Rserve(" + (if (debug) "TRUE" else "FALSE") + ",args=\"" + rsrvargs + "\")'|" + cmd + " " + rargs))
  }

  def info(str:String) =   System.out.println(str)


  /** attempt to start Rserve. Note: parameters are <b>not</b> quoted, so avoid using any quotes in arguments
     @param cmd command necessary to start R
  @param rargs arguments are are to be passed to R
  @param rsrvargs arguments to be passed to Rserve
  @return <code>true</code> if Rserve is running or was successfully started, <code>false</code> otherwise.
    */
  def launchRserve(cmd: String, rargs: String, rsrvargs: String, debug: Boolean): Try[Unit] = {
    Try {
      val wind: Boolean = this.isWindows
      val p: Process = this.getProcess(wind, cmd, rargs, rsrvargs, debug)
      System.out.println("waiting for Rserve to start ... (" + p + ")")
      val errorHog: StreamHog = new StreamHog(p.getErrorStream, false)
      val outputHog: StreamHog = new StreamHog(p.getInputStream, false)
      if (!wind) p.waitFor
      info("call terminated, let us try to connect ...")
    }.recover{case f=>
      info("failed to start Rserve process with " + f.getMessage)
      f
    }
  }
  implicit protected val connectionRight =  retry.Success.apply[RConnection](r=>Try(r).isSuccess)

  def getConnection(maxAttempts:Int = 5,delay:FiniteDuration = 100.milliseconds) =
    retry.Pause(maxAttempts, delay).apply{Future(new RConnection)}(connectionRight,scala.concurrent.ExecutionContext.Implicits.global)

  def isReady(maxAttempts:Int = 5,delay:FiniteDuration = 100.milliseconds): Future[Boolean] = getConnection(maxAttempts,delay).map{
    case  con=> con.close()
      info("RServe is running")
      true
    } recover {
      case  th=>
      info(s"RServe failed $maxAttempts attempts with $delay delay")
      false
    }

  def launchIfExist(path:String):Try[Unit]= {
    new File(path).exists() match {
      case true=>this.launchRserve(path)
      case false=>Failure(new Exception(s"$path does not exist"))
    }
  }

  protected def getWindowsPath = Try{
      val rp: Process = Runtime.getRuntime.exec("reg query HKLM\\Software\\R-core\\R")
      val regHog: StreamHog = new StreamHog(rp.getInputStream, true)
      rp.waitFor
      regHog.join()
      regHog.getInstallPath
    } recoverWith{ case rge=>
      info("ERROR: unable to run REG to find the location of R: " + rge)
      Failure(rge)
    }



  /** checks whether Rserve is running and if that's not the case it attempts to start it using the defaults for the platform where it is run on.
    *  This method is meant to be set-and-forget and cover most default setups. 
    *  For special setups you may get more control over R with <<code>launchRserve</code> instead. */
  def runLocalRserve: Future[Boolean] = isReady(3,100.milliseconds) map {
    case true=>true
    case false=>
      val run = if (isWindows)
        getWindowsPath flatMap  {     result=>launchRserve(result)}
      else  launchRserve("R")
      .recoverWith{case th=>  this.launchIfExist("/Library/Frameworks/R.framework/Resources/bin/R")}
      .recoverWith{case th=>  this.launchIfExist("/usr/local/lib/R/bin/R")}
      .recoverWith{case th=>  this.launchIfExist("/usr/lib/R/bin/R")}
      .recoverWith{case th=>  this.launchIfExist("/usr/local/bin/R")}
      .recoverWith{case th=>  this.launchIfExist("/sw/bin/R")}
      .recoverWith{case th=>  this.launchIfExist("/usr/common/bin/R")}
      .recoverWith{case th=>  this.launchIfExist("/opt/bin/R")}
      run match {
        case Success(res)=>true
        case Failure(th)=>
          info(s"cannot start local RServe because of $th")
          false
      }
  }

  /** check whether Rserve is currently running (on local machine and default port).
     @return <code>true</code> if local Rserve instance is running, <code>false</code> otherwise
    */
  def isRserveRunning: Boolean = {
    try {
      val c: RConnection = new RConnection
      System.out.println("Rserve is running.")
      c.close
      return true
    }
    catch {
      case e: Exception =>
        System.out.println("First connect try failed with: " + e.getMessage)
    }
    false
  }

}
