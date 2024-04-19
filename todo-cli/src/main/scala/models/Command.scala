package models

trait Command
case class Add(task: String) extends Command
case class Remove(taskNumber: String) extends Command
case object View extends Command
case object Quit extends Command
case object Help extends Command
case object Unknown extends Command

object Command {
  def parse(s: String): Command = s match {
    case s"add ${task}" => Add(task)
    case s"rm ${taskNum}" => Remove(taskNum)
    case s"remove ${taskNum}" => Remove(taskNum)
    case "h" | "help" => Help
    case "v" | "view" => View
    case "q" | "quit" => Quit
    case _ => Unknown
  }
}
