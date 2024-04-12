import scala.annotation.tailrec

object Main extends App {
  def fib(n: Int) = {
    @tailrec
    def run(n: Int, prev: Int, curr: Int): Int = {
      if (n <= 0)
        curr
      else
        run(n - 1, prev = prev + curr, curr = prev)
    }
    run(n, 1,0 )
  }

  println(fib(6))
}