package test

import java.io.File

import test.utils.Parser

import scala.util.{Failure, Success, Try}


object SimpleSearch extends App {
  Program
    .readFile(args)
    .fold(
      println,
      file => Program.iterate(Program.index(file))
    )
}

object Program {
  import scala.io.StdIn.readLine
  import test.models._

  implicit val parser: Parser = Parser()
  val PROMPT = "search> "
  val TERMINATOR = ":quit"
  val NO_MATCH = "no matches found"

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
    print(PROMPT)
    val searchString = readLine()
    if (searchString != TERMINATOR) {
      val words = parser.parseToWords(searchString).toList
      val result = Search(index, words).result
      if (result.nonEmpty) result.foreach(println)
      else println(NO_MATCH)
      iterate(index)
    }
  }
}
