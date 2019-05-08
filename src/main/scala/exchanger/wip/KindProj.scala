package exchanger.wip

import cats.Monad
import cats.effect.Effect
import monix.eval.Task

object KindProj extends App{

  trait TestAlgebra[F[_], A, B]{
    def get(a: A): F[B]
    def set (a: A)(b: B): F[Unit]
  }

  val test = new TestAlgebra[Task, Int, String] {
    override def get(a: Int): Task[String] = Task{"string"}
    override def set(a: Int)(b: String): Task[Unit] = Task{println(s"$a, $b")}
  }

  import monix.execution.Scheduler.Implicits.global

  class newTest[F[_]:Effect]
    extends TestAlgebra[F, Int, String]{
    override def get(a: Int): F[String] = implicitly[Effect[F]].pure("asd")

    override def set(a: Int)(b: String): F[Unit] = implicitly[Monad[F]].pure(println(s"$a, $b"))
  }

  val test2 = new newTest[Task]


  println("this first")
  test2.set(1)("kek").runSyncUnsafe()
}
