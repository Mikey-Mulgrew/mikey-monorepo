import models.TodoItem
import services.StateService

object Main extends App {
  val ps = new StateService

  val tdi = TodoItem("title", "desc", false)

  println(ps.writeToFile(List(tdi)).toOption.getOrElse("Something Went Wrong!"))
}