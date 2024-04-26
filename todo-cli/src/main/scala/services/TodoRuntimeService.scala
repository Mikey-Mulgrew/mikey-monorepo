package services

import cats.effect.IO
import models._

class TodoRuntime(console: Console, stateService: IOService) {
  private val prompt = "Command ('h' for help, 'q' to quit)\n==> "

  def mainLoop(): IO[Unit] = for {
    _ <- console.printLine(prompt)
    cmd <- console.readLine.map(Command.parse)
    _ <- cmd match {
      case Quit => IO.unit
      case _ => processCommand(cmd) >> mainLoop //note the recursion here
    }
  } yield ()

  private def processCommand(cmd: Command) : IO[Unit] = cmd match {
    case Add(title, description) => add(title, description)
    //case Remove(n) => remove(n.toInt)
    case Help => help
    case View => view
    case Unknown => {
      console.printLine("Unknown (type 'h' for help)\n")
    }
  }

  private def add(task: String, description:String): IO[Unit] = TodoItem.parseItem(task, description) match {
    case Some(td) =>  stateService.writefile(td, true)
    case None => console.printLine("add <title> <description>")
  }

  private def view: IO[Unit] = for {
    lines <- stateService.readFile
    result <- IO((for ((line,i) <- lines.zip(LazyList from 1)) yield s"$i. $line").mkString("\n"))
    _ <- console.printLine(result)
  } yield ()

  def help: IO[Unit] = {
    val text =
      """
        |Possible commands
        |-----------------
        |add <title> <description>       - add a to-do item
        |h                - show this help text
        |rm [task number] - remove a task by its number
        |v                - view the list of tasks
        |q                - quit
      """.stripMargin
    console.printLine(text)
  }

  /**
   * remove the given task number from the file.
   * `taskToRemove` will be based on 1,2,3.
   * however, the list will be zero-based.
   */
//  def remove(taskNumToRemove: Int): IO[Unit] = for {
//    currentTasksAsIO <- stateService.readFile(datafile)
//    currentTasks <- IO(currentTasksAsIO.toVector)
//    remainingTasks <- IO(removeElementFromSequence(currentTasks, taskNumToRemove - 1))
//    remainingTasksAsString <- IO(remainingTasks.mkString("\n"))
//    _ <- stateService.writefile(datafile, remainingTasksAsString, false)
//  } yield ()

  def removeElementFromSequence(seq: Seq[String], index: Int): Seq[String] = {
    if (index < 0 || index >= seq.length) {
      seq
    } else if (index == 0) {
      seq.tail
    } else {
      val (a, b) = seq.splitAt(index)
      a ++ b.tail
    }
  }
}
