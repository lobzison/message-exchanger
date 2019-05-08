package exchanger.wip

import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import cats.effect.Resource
import doobie.implicits._

object TaskImplementations extends App{

  import monix.eval.Task
  import cats.implicits.toFunctorOps

  val transactor: Resource[Task, HikariTransactor[Task]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[Task](32) // our connect EC
      te <- ExecutionContexts.cachedThreadPool[Task] // our transaction EC
      xa <- HikariTransactor.newHikariTransactor[Task](
        "oracle.jdbc.driver.OracleDriver", // driver classname
        "jdbc:oracle:thin:@0.0.0.0:32769:ORCLCDB", // connect URL
        "SYS as SYSDBA", // username
        "Oradoc_db1", // password
        ce, // await connection here
        te // execute JDBC operations here
      )
    } yield xa

  val query = sql"select level from dual connect by level <= 1e7".query[Int].stream

  import fs2.Stream
  val file: java.io.File = new java.io.File("/Users/lobzison/code/src/github.com/lobzison/resources/test_output")
  val mqResource = Resource.make(Task(new java.io.FileOutputStream(file)))(fos => Task{fos.close(); println("closed file")})

  import cats.implicits.catsSyntaxSemigroupal


  val combined = transactor product mqResource

  import java.io.FileOutputStream
  val streamWithRes: Stream[Task, (HikariTransactor[Task], FileOutputStream)] = Stream.resource(combined)

  val streamBoth = for {
    res <- streamWithRes
    queryRes <- query.transact(res._1)
    write <- Stream.eval(Task{res._2.write(queryRes.toString.getBytes()); res._2.flush()})
  } yield write

  println("Started")
  val task = streamBoth.compile.drain

  import monix.execution.Scheduler.Implicits.global
  task.runSyncUnsafe()



}