package test.utils

class Parser {

  val WORD = "\\w+".r

  def parseToWords(text: String) =
    WORD.findAllIn(text).map(_.toLowerCase)
}

object Parser {
  def apply() = new Parser
}
