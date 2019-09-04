package test

import java.io.File

import test.utils.Parser

import scala.util.{Failure, Success, Try}


object SimpleSearch extends App {
  Program
    .readFile(args = Array("/Users/mkumar/tmp/"))
    .fold(
      println,
      file => Program.iterate(Program.index(file))
    )
}

object Program {

  import scala.io.StdIn.readLine
  import test.models._

  implicit val parser: Parser = Parser()

  def index(sourceDir: File): Index = {
    val _index = Index.indexAllFilesInDir(sourceDir)
    println(s"${_index.files.size} files read in directory ${ sourceDir.getAbsolutePath }")
    _index
  }

  def readFile(args: Array[String]): Either[ReadFileError, File] = {
    for {
      path <- args.headOption.toRight(MissingPathArg)
      file <- Try(new java.io.File(path))
        .fold(
          throwable =>
            Left(FileNotFound(throwable)),
          file =>
            if (file.isDirectory) Right(file)
            else Left(NotDirectory(s"Path [$path] is not a directory"))
        )
    } yield file
  }

  def iterate(index: Index): Unit = {
    print(s"search> ")
    val searchString = readLine()
    if (searchString != ":quit") {
      // TODO: Make it print the ranking of each file and its corresponding score
      val words = parser.parseToWords(searchString).toList
      Search(index, words).result.foreach(println)
      iterate(index)
    }
  }

  class Search(index: Index, words: List[String]) {

    def result = {
      val wordsMapping = getWordFilesMapping
      val files = wordsMapping.flatMap(_._2).groupBy(identity).mapValues(_.size)
      files
    }

    private val getWordFilesMapping = words.flatMap { word =>
      index.invertedIndex
        .get(word)
        .map( f => (word, f.map(_.getName).toList))
    }
  }

  object Search {
    def apply(index: Index, words: List[String]) = new Search(index, words)
  }
}
