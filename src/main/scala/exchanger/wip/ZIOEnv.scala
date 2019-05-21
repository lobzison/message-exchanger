package exchanger.wip



import scalaz.zio.{DefaultRuntime, ZIO, clock}
import scalaz.zio.clock.Clock
import scalaz.zio.console._

object ZIOEnv extends App {

  println("Testing ZIO R type parameter")

  val programm: ZIO[Clock, Throwable, Long] = ZIO.accessM(_.clock.nanoTime)


  val loggingProgramm: ZIO[Clock, Throwable, String] = for {
    time <- clock.nanoTime
    message <- ZIO.succeed(s"current time is $time")
  } yield message

  val consoleProgram: ZIO[Console, Throwable, Unit] = for {
    _ <- putStrLn("sup")
    name <- getStrLn
    _ <- putStrLn(s"Sup bitch $name")
  } yield ()

  case class Config(login: String, password: String)

  def dummy(s: String): Unit = ()

  val confingProgramm: ZIO[Config, Throwable, Unit] = for {
    _ <- ZIO.access[Config](_.login)
  } yield ()

  val combines =
    loggingProgramm *> confingProgramm

  val runtime = new DefaultRuntime {}


  runtime.unsafeRunSync(consoleProgram)

}
