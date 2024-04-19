package models
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

case class TodoItem(title: String, description: String, complete: Boolean)

object TodoItem {
  def toJson(todoItem: TodoItem): Json = todoItem.asJson
  def fromJson(raw: String): Option[List[TodoItem]] = decode[List[TodoItem]](raw).toOption

  def parse(title: String, description: String): Option[TodoItem] = Some(TodoItem(title, description, false))

}
