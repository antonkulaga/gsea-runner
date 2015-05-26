package org.denigma.gsea.views

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter._

object Repl extends App {
  def repl = new ILoop {

  }

  val settings = new Settings
  settings.Yreplsync.value = true


  //use when launching normally outside SBT
  settings.usejavacp.value = true

  //an alternative to 'usejavacp' setting, when launching from within SBT
  //settings.embeddedDefaults[Repl.type]

  repl.process(settings)
  repl.printWelcome()
}