package test

import java.io.File

import test.utils.Parser

import scala.util.Try


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


  // TODO: Index all files in the directory
  def index(file: File): Index = {
    // println("Generating index...")

    val files = file.listFiles
    var invertedIndex : InvertedIndex = Map.empty

    files.foreach { innerFile =>
      val source = scala.io.Source.fromFile(innerFile.getAbsolutePath, "ISO-8859-1") // handle encoding errors
      try {
        source.getLines.flatMap { line =>
          Parser().parseToWords(line)
        }.foreach { word =>
          val newFileList = invertedIndex.getOrElse(word, Set()) + innerFile
          invertedIndex += (word -> newFileList)
        }
      }
      finally source.close()
    }
    println(s"${files.length} files read in directory ${ file.getAbsolutePath }")
    Index(invertedIndex)
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

  def iterate(indexedFiles: Index): Unit = {
    print(s"search> ")
    val searchString = readLine()
    if (searchString != ":quit") {
      // TODO: Make it print the ranking of each file and its corresponding score
      Parser().parseToWords(searchString).foreach { word =>
        val files = indexedFiles.invertedIndex.get(word).map { files =>
          val names = files.map(_.getName).toList.take(10)
          println(s"$word : ${ names.mkString(", ") }")
        }
      }

      iterate(indexedFiles)
    }
  }
}
