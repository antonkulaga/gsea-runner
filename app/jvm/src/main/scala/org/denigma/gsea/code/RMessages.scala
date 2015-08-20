package org.denigma.gsea.code

import org.rosuda.REngine.REXP

object RMessages {

  case class Code(str:String)

  case class Result(r:REXP)
}
