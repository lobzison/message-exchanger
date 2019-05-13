package exchanger.wip

import scalaz.zio.{App, Task}
import scalaz.zio.console._
//import scalaz.zio.blocking._

object Interrupt extends App {

  override def run(args: List[String]) = programm.fold(_ => 1, _ => 0)

  val res = Task.succeed(42)

  val programm = Task.bracket(res)(x => putStr(s"Closing $x")){ res =>
    Thread.sleep(10000);
    Task.succeed(res)
  }

}
