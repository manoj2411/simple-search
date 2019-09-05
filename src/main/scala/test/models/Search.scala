package test.models

case class Search(index: Index, words: List[String]) {

  def result: List[Result] = {
    val fileAndCounts = getWordFilesMapping
      .flatMap(_._2)
      .groupBy(identity)
      .mapValues(_.size)
    buildResult(fileAndCounts)
  }

  def buildResult(mapping: Map[String, Int]): List[Result] =
    sortByScore(calculateScores(mapping))

  def calculateScores(fileAndCounts: Map[String, Int]): List[Result] =
    fileAndCounts.map( pair =>
      Result(pair._1, (100 * (pair._2.toDouble / words.length)).toInt)
    ).toList

  def sortByScore(result: List[Result], limit: Int = 10):List[Result] =
    result.sortWith(_.score > _.score).take(limit)

  private val getWordFilesMapping = words.flatMap { word =>
    index.invertedIndex
      .get(word)
      .map( f => (word, f.map(_.getName).toList))
  }
}
