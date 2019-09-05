package test.models

case class Result(file: String, score: Int) {
  override def toString = s"$file : $score"
}