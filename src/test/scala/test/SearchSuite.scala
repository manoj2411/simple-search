package test

import org.scalatest.FunSuite
import test.models.{Index, Search}
import test.utils.Parser

class SearchSuite extends FunSuite {
  implicit val parser = Parser()
  val words = List("hello", "world", "a")

  test ("scoring with zero counts ") {
    val mapping = Map("file1" -> 0, "file2" -> 0)
    val result = Search(Index(), words).calculateScores(mapping)
    result.foreach { r => assert(r.score == 0 ) }
  }

  test ("scoring with empty mapping") {
    val result = Search(Index(), words).calculateScores(Map[String, Int]())
    result.foreach { r => assert(r.score == 0 ) }
  }

  test ("scoring with valid mapping") {
    val mapping = Map("file1" -> 2, "file2" -> 3, "file3" -> 1)
    val result = Search(Index(), words).calculateScores(mapping)
    result.filter(_.file == "file2").foreach( x => assert(x.score == 100))
    result.filter(_.file == "file1").foreach( x => assert(x.score == 66))
    result.filter(_.file == "file3").foreach( x => assert(x.score == 33))
  }

  test ("results should be sorted") {
    val mapping = Map("file1" -> 2, "file2" -> 3, "file3" -> 1)
    val result = Search(Index(), words).buildResult(mapping)
    assert( result.map(_.score) == List(100, 66, 33))
  }

}
