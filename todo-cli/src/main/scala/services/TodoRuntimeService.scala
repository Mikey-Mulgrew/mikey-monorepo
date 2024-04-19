package services

import cats.effect.IO
import models._
import cats.implicits._

trait Console {
  def readLine: IO[String]
  def printLine(s: String): IO[Unit]
}

class LiveConsole extends Console {
  override def readLine: IO[String] = IO(scala.io.StdIn.readLine())

  override def printLine(s: String): IO[Unit] = IO(println(s))
}

class TodoRuntime(console: Console, stateService: IOService) {
  private val datafile = "src/main/resources/todos.dat"
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
    case Add(result) => add(result)
    case Remove(n) => remove(n.toInt)
    case Help => help
    case View => view
    case Unknown => {
      console.printLine("Unknown (type 'h' for help)\n")
    }
  }

  private def add(task: String): IO[Unit] = stateService.writefile(datafile, task, true)

  private def view: IO[Unit] = for {
    lines <- stateService.readFile(datafile)
    result <- IO((for ((line,i) <- lines.zip(LazyList from 1)) yield s"$i. $line").mkString("\n"))
    _ <- console.printLine(result)
  } yield ()

  def help: IO[Unit] = {
    val text =
      """
        |Possible commands
        |-----------------
        |add <task>       - add a to-do item
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
  def remove(taskNumToRemove: Int): IO[Unit] = for {
    currentTasksAsIO <- stateService.readFile(datafile)
    currentTasks <- IO(currentTasksAsIO.toVector)
    remainingTasks <- IO(removeElementFromSequence(currentTasks, taskNumToRemove - 1))
    remainingTasksAsString <- IO(remainingTasks.mkString("\n"))
    _ <- stateService.writefile(datafile, remainingTasksAsString, false)
  } yield ()

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
