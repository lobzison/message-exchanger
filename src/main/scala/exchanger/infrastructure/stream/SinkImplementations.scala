package exchanger.infrastructure.stream

import java.io.FileOutputStream

import cats.effect.Resource
import exchanger.config.Config.FileConfig
import fs2.Stream
import monix.eval.Task

object SinkImplementations {
  implicit val fileSink: Sink[Task, FileOutputStream, FileConfig, Int] =
    new Sink[Task, FileOutputStream, FileConfig, Int] {
      override def createResource(config: FileConfig): Resource[Task, FileOutputStream] = {
        val file: java.io.File =
          new java.io.File(config.filePath)
        Resource.make(Task(new java.io.FileOutputStream(file)))(fos => Task {
          fos.close(); println("closed file")
        })
      }
      override def createStream(resource: FileOutputStream, input: Int): Stream[Task, Unit] =
        Stream.eval(Task{resource.write(input.toString.getBytes()); resource.flush()})
    }
}
