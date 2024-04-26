package models
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser._
import io.circe.syntax._

case class TodoItem(title: String, description: String, complete: Boolean)

object TodoItem {

  implicit val todoDecoder: Decoder[TodoItem] = deriveDecoder
  implicit val todoEncoder: Encoder[TodoItem] = deriveEncoder

  def toJson(todoItem: TodoItem): Json              = todoItem.asJson
  def fromJson(raw: String): Option[List[TodoItem]] = decode[List[TodoItem]](raw).toOption

  def parseItem(
      title: String,
      description: String,
      complete: Option[Boolean] = None
  ): Option[TodoItem] = Some(TodoItem(title, description, complete.getOrElse(false)))

}
