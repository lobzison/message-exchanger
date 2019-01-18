package exchanger.domain

object FlowResource extends App{
  trait Flow[S[_, _], R[_, _], F[_]] {
//    def combineResources[A,B](r1: R[F, A], r2: R[F, B]): R[F, (A,B)]
//    def combineStreams[A, B](s1: S[F, A], s2: S[F, A => B]): S[F, B]
//    def streamsWithResources[A, B](r: R[F, A], s: S[F, B])(f: A => B): S[F, B]
  }
  object Flow{
    def apply[S[_, _], R[_, _], F[_]](implicit flow: Flow[S, R, F]):Flow[S, R, F]  = flow
  }
//
//  import fs2.Stream
//  import cats.effect.{Resource, IO}
//  implicit val resStreamIO: Flow[Stream, Resource, IO] = new Flow[Stream, Resource, IO] {
//    override def combineResources[A, B](r1: Resource[IO, A], r2: Resource[IO, B]): Resource[IO, (A, B)] =
//      for {
//      res1 <- r1
//      res2 <- r2
//    } yield (res1, res2)
//
//    override def combineStreams[A, B](s1: Stream[IO, A], s2: Stream[IO, A => B]): Stream[IO, B] = ???
//    override def streamsWithResources[A, B](r: Resource[IO, A], s: Stream[IO, B])(f: A => B): Stream[IO, B] = for {
//      resStream <- Stream.resource(r)
//    } yield f(resStream)
//  }

}
