package test.models

import java.io.File

import test.InvertedIndex
import test.utils.Parser

import scala.util.Try


case class Index(files: Set[File] = Set(), invertedIndex: InvertedIndex = Map.empty)(implicit parser: Parser) {

  def addToIndex(word: String, file: File): Index = {
    val newFileList = invertedIndex.getOrElse(word, Set()) + file
    Index(files + file, invertedIndex + (word -> newFileList))
  }

}

object Index {

  def indexAllFilesInDir(sourceDir: File)(implicit parser: Parser): Index = {

    val files = sourceDir.listFiles.filter(_.isFile) // TODO: handle directories (nesting)

    files.foldLeft(Index()) { (acc, file) =>
      readFile(file) match {
        case Left(error) =>
          println(s"Indexing failed for file $file because ${error}")
          acc
        case Right(lines) =>
          lines.flatMap(parser.parseToWords).foldLeft(acc) { (accIndex, word) =>
            accIndex.addToIndex(word, file)
          }
      } }
  }

  def readFile(file: File): Either[ReadFileError, List[String]] = {
    val source = scala.io.Source.fromFile(file.getAbsolutePath, "ISO-8859-1") // handle encoding errors
    val result = Try(source.getLines.toList).fold(
      throwable => Left(InvalidEncoding(throwable.getMessage)),
      text => Right(text)
    )
    source.close()
    result
  }
}