package test

import java.io.File

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

  sealed trait ReadFileError

  case object MissingPathArg extends ReadFileError
  case class NotDirectory(error: String) extends ReadFileError
  case class FileNotFound(t: Throwable) extends ReadFileError

  // TODO: Implement this
  case class Index()

  // TODO: Index all files in the directory
  def index(file: File): Index = {
    println("Generating index...")
    val files = file.listFiles
    files.foreach { innerFile =>
      val source = scala.io.Source.fromFile(innerFile.getAbsolutePath, "ISO-8859-1") // handle encoding errors
      val lines = try source.mkString finally source.close()
      println(innerFile.getName + "\t" + lines.length)
    }
    Index()
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
      iterate(indexedFiles)
    }
  }
}