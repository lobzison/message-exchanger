package exchanger.infrastructure.stream

import cats.effect.Resource
import fs2.Stream

trait Source[F[_], Res, Conf, Out] {
  def createResource(config: Conf): Resource[F, Res]
  def createStream(resource: Res): Stream[F, Out]
}
 object Source {
   def apply[F[_], Res, Conf, Out](implicit S: Source[F, Res, Conf, Out]): Source[F, Res, Conf, Out] = S
 }

//abstract class Flow[F[_]] {
//  def combineResources[Res1, ](implicit r1: Source[F,A, B])
//}