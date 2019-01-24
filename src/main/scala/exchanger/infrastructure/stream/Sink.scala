package exchanger.infrastructure.stream

import cats.effect.Resource
import fs2.Stream

trait Sink[F[_], Res, Conf, In] {
  def createResource(config: Conf): Resource[F, Res]
  def createStream(resource: Res, input: In): Stream[F, Unit]
}
object Sink {
  def apply[F[_], Res, Conf, In](implicit S: Sink[F, Res, Conf, In]): Sink[F, Res, Conf, In] = S
}

