package exchanger

import cats.effect.{IO, Resource}

object MessageExchanger extends App{
//  import cats._
  import cats.implicits._
//  import fs2.Pipe
  import fs2.Stream

  case class Connection(name: String) {
    def getValue: Int = 423
    def putValue(i: Int): Unit = ()
  }


  val stream1 = Stream.eval(IO(println(1)))

  val res1 = Resource.make(IO{println("open res1"); Connection("res1")})(_ => IO(println("close res1")))

  val res2 = Resource.make(IO{println("open res2"); Connection("res2")})(_ => IO(println("close res2")))

  //combining two resources
  val resPair = for {
    source <- res1
    sink <- res2
  } yield (source, sink)

  val streamWithRes = Stream.resource(resPair).flatMap{case (_, _) => Stream(1,2,3)}

}
