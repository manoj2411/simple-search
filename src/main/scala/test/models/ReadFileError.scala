package test.models

sealed trait ReadFileError

case object MissingPathArg extends ReadFileError
case class NotDirectory(error: String) extends ReadFileError
case class FileNotFound(t: Throwable) extends ReadFileError
