package services

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import models.TodoItem

import java.io._
import scala.io.Source

class IOService(dataSource: String) {

  val makeResourceForRead: Resource[IO, Source] =
    Resource.make(sourceIO)(src => closeFile(src))

  def sourceIO: IO[Source] =
    IO.println("Acquiring source to read file") >> IO(Source.fromFile(dataSource))

  def readFile: IO[Seq[String]] =
    IO(makeResourceForRead.use(src => readLines(src)).unsafeRunSync())

  def readLines(source: Source): IO[Seq[String]] =
    IO.println("Reading contents from the file") >> IO(source.getLines().toVector)

  def closeFile(source: Source): IO[Unit] =
    IO.println("Closing the file source") >> IO(source.close())

  def writefile(todoItem: TodoItem, append: Boolean): IO[Unit] = {
    def writerIO: IO[FileWriter] =
      IO.println("Acquiring source to write file") >> IO(
        new FileWriter(dataSource, append)
      )
    def writeLines(writer: FileWriter, todoItem: TodoItem): IO[Unit] =
      IO.println("Writing the contents to file") >> IO {
        writer.write(TodoItem.toJson(todoItem).toString())
      }
    def closeWriteFile(writer: FileWriter): IO[Unit] =
      IO.println("Closing the file writer") >> IO(writer.close())

    val makeResourceForWrite: Resource[IO, FileWriter] =
      Resource.make(writerIO)(src => closeWriteFile(src))

    for {
      content <- readFile
      _ <- makeResourceForWrite.use(fw => writeLines(fw, todoItem))
    } yield ()
  }
}
