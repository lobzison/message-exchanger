package exchanger.infrastructure.stream

import cats.effect.Resource
import doobie.hikari.HikariTransactor
import doobie.implicits._
import exchanger.config.Config.DbConfig
import monix.eval.Task
import fs2.Stream
import cats.implicits.toFunctorOps

object SourceImplementations {
  implicit val dbSource: Source[Task, HikariTransactor[Task], DbConfig, Int] =
    new Source[Task, HikariTransactor[Task], DbConfig, Int] {
      import doobie.ExecutionContexts

      override def createStream(resource: HikariTransactor[Task]): Stream[Task, Int] =
        sql"select level from dual connect by level <= 1e7".query[Int].stream.transact(resource)

    override def createResource(config: DbConfig): Resource[Task, HikariTransactor[Task]] =  for {
      ce <- ExecutionContexts.fixedThreadPool[Task](32) // our connect EC
      te <- ExecutionContexts.cachedThreadPool[Task] // our transaction EC
      xa <- HikariTransactor.newHikariTransactor[Task](
        config.driverClassName, // driver classname
        config.url, // connect URL
        config.user, // username
        config.password, // password
        ce, // await connection here
        te // execute JDBC operations here
      )
    } yield xa
  }



}
