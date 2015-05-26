package org.denigma.gsea.views

import java.util.Date


case class CodeCell(code:String,result:String,time:Date,previous:Option[CodeCell] = None)
