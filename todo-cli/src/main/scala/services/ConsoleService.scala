package services

import cats.effect.IO

trait Console {
  def readLine: IO[String]
  def printLine(s: String): IO[Unit]
}

class LiveConsole extends Console {
  override def readLine: IO[String] = IO(scala.io.StdIn.readLine())

  override def printLine(s: String): IO[Unit] = IO(println(s))
}