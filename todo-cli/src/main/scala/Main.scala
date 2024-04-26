import cats.effect.unsafe.implicits.global
import services.{TodoRuntime, LiveConsole, IOService}

object Main extends App {
  private val datafile = "src/main/resources/todos.dat"
  val stateService = new IOService(datafile)
  val console = new LiveConsole

  val ioService = new TodoRuntime(console, stateService)

  ioService.mainLoop().unsafeRunSync()
}
