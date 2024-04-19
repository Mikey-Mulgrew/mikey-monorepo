package services

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}

import java.io._
import scala.io.Source

class IOService {

  def readFile(filename: String): IO[Seq[String]] = {
    def sourceIO: IO[Source] =
      IO.println("Acquiring source to read file") >> IO(
        Source.fromFile(filename)
      )
    def readLines(source: Source): IO[Seq[String]] =
      IO.println("Reading contents from the file") >> IO(
        source.getLines().toVector
      )
    def closeFile(source: Source): IO[Unit] =
      IO.println("Closing the file source") >> IO(source.close())

    IO {
      val makeResourceForRead: Resource[IO, Source] =
        Resource.make(sourceIO)(src => closeFile(src))

      makeResourceForRead.use(src => readLines(src)).unsafeRunSync()
    }
  }

  def writefile(filename: String, text: String, append: Boolean): IO[Unit] = {
    def writerIO: IO[FileWriter] =
      IO.println("Acquiring source to write file") >> IO(
        new FileWriter(filename, append)
      )
    def writeLines(writer: FileWriter, content: String): IO[Unit] =
      IO.println("Writing the contents to file") >> IO(writer.write(content + "\n"))
    def closeWriteFile(writer: FileWriter): IO[Unit] =
      IO.println("Closing the file writer") >> IO(writer.close())

    IO {
      val makeResourceForWrite: Resource[IO, FileWriter] =
        Resource.make(writerIO)(src => closeWriteFile(src))

      makeResourceForWrite.use(fw => writeLines(fw, text)).unsafeRunSync()
    }
  }
}
