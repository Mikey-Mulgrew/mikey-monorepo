package services

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.TodoItem

import java.io._
import scala.io.Source

class StateService {
  val todoPath = "src/main/resources/todos.json"

  def fetchFile(): Either[Error, List[TodoItem]] = {
    val stateFile = Source.fromFile(todoPath)
    val raw = stateFile.getLines().mkString
    stateFile.close()
    decode[List[TodoItem]](raw)
  }

  def writeToFile(todoItem: List[TodoItem]): Either[Error, List[TodoItem]] = {
    val pw = new PrintWriter(new File(todoPath))
    val parsed = todoItem.asJson.toString()
    pw.write(parsed)
    pw.close()

    fetchFile()
  }
}
