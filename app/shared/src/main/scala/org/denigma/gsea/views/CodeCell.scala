package org.denigma.gsea.views

import java.util.{UUID, Date}

object CodeCell{
  def empty(previous:CodeCell) = CodeCell("","",new Date)
  lazy val empty = CodeCell("","",new Date)
}
case class CodeCell(code:String,result:String,time:Date,id:UUID = UUID.randomUUID())
