import cats.effect.unsafe.implicits.global
import services.{TodoRuntime, LiveConsole, IOService}

object Main extends App {
  val stateService = new IOService
  val console = new LiveConsole

  val ioService = new TodoRuntime(console, stateService)

  ioService.mainLoop().unsafeRunSync()
}
