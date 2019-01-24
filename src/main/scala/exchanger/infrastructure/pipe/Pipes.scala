package exchanger.infrastructure.pipe

import java.io.FileOutputStream

import exchanger.infrastructure.stream.SinkImplementations._
import exchanger.infrastructure.stream.SourceImplementations._
import cats.implicits.catsSyntaxSemigroupal
import doobie.hikari.HikariTransactor
import exchanger.config.Config.{DbConfig, FileConfig}
//import exchanger.infrastructure.stream.{Sink, Source}
import monix.eval.Task
import fs2.Stream
object Pipes extends App {

  val dbConf = DbConfig(
    "oracle.jdbc.driver.OracleDriver",
    "jdbc:oracle:thin:@0.0.0.0:32769:ORCLCDB",
    "SYS as SYSDBA",
    "Oradoc_db1")

  val fileConfig = FileConfig("/Users/lobzison/code/src/github.com/lobzison/resources/test_output")

  val dbRes = dbSource.createResource(dbConf)
  val fileRes = fileSink.createResource(fileConfig)

  val combined: Stream[Task, (HikariTransactor[Task], FileOutputStream)] = Stream.resource(dbRes product fileRes)

  val streamBoth = for {
    res <- combined
    data <- dbSource.createStream(res._1)
    stream <- fileSink.createStream(res._2, data)
  } yield stream

  //import monix.execution.Scheduler.Implicits.global
  //streamBoth.compile.drain.runSyncUnsafe()


//  abstract class Flow[F[_]]{
//    def createStream[A:Source[_,_,_,_], B:Sink[_,_,_,_], C, D]
//    (source: A, sink: B)
//    (sourceConf: C, sinkConf:D): Stream[F, Unit]
//  }

//def createStream[F[_]](a: Source[_,_,_,_]):Stream[F, Unit]

}
