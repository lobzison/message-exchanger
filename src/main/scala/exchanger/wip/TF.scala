package exchanger.wip

object TF extends App{

  case class MYIO[A](a:() => A){
    def execute:A = a()
    def id(a: A):MYIO[A] = MYIO(() => a)
    def map[B](f: A => B):MYIO[B] = MYIO(() => f(a()))
    def flatMap[B](f: A => MYIO[B]): MYIO[B] = MYIO(() => f(a()).execute)
  }

  //typeclass
  trait ConsolePrint[F[_]]{
    def printLine[A](a: A): F[Unit]
    def redLine():F[String]
  }
  object ConsolePrint{
    def apply[F[_]](implicit F: ConsolePrint[F]): ConsolePrint[F] = F
  }


  // instance of typeclass ConsolePrint for IO
//  implicit val ioPrinter: ConsolePrint[MYIO] = new ConsolePrint[MYIO] {
//    override def printLine[A](a: A): MYIO[Unit] = MYIO(() => println(a))
//    override def redLine(): MYIO[String] = MYIO(() => scala.io.StdIn.readLine())
//  }

  import cats.effect.IO

  implicit val IOPrinter: ConsolePrint[IO] = new ConsolePrint[IO] {
    override def printLine[A](a: A): IO[Unit] = IO(println(a))
    override def redLine(): IO[String] = IO(scala.io.StdIn.readLine())
  }

  //use some thing where ConsolePrint is defined
  def doStuff[F[_]: ConsolePrint](i: Int): F[Unit] = {
    ConsolePrint[F].printLine(i)
  }

  doStuff(10).unsafeRunSync()

}
