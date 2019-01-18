package exchanger.infrastructure.stream

//import java.io.File

import java.io.FileOutputStream

import cats.effect._
import cats.effect.internals.IOContextShift
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.hikari._

object Implementations extends App{
  implicit val cs: ContextShift[IO] = IOContextShift.global

  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      te <- ExecutionContexts.cachedThreadPool[IO] // our transaction EC
      xa <- HikariTransactor.newHikariTransactor[IO](
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
  //println(Stream.resource(transactor).flatMap(xa => query.transact(xa)).compile.toList.unsafeRunSync())

  val file: java.io.File = new java.io.File("/Users/lobzison/code/src/github.com/lobzison/resources/test_output")
  val mqResource = Resource.fromAutoCloseable(IO(new java.io.FileOutputStream(file)))

  val testResource = Resource.make(IO(StupidRes("test")))(a => IO(a.close()))

  val combinedResource = for {
    source <- transactor
    sink <- mqResource
  } yield (source, sink)

  val sreamBoth = Stream.resource(combinedResource).flatMap{
    case (db, _) =>
      query.transact(db)
  }.through(input => input.flatMap(value => Stream.resource(mqResource).map(f => f.write(value))))

  //val res = sreamBoth.compile.drain.unsafeRunSync()

  val streamWithRes: Stream[IO, (HikariTransactor[IO], FileOutputStream)] = Stream.resource(combinedResource)

  val streamBoth = for {
    res <- streamWithRes
    queryRes <- query.transact(res._1)
    write <- Stream.eval(IO{res._2.write(queryRes.toString.getBytes()); res._2.flush()})
  } yield write

  println("start")
  streamBoth.compile.drain.unsafeRunSync()

  case class StupidRes(path: String) {
    def write[A](a: A): Unit = println(a)
    def close(): Unit = println("closing")
  }

}
