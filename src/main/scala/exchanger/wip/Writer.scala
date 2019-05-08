package exchanger.wip

object Writer extends App{
  import java.io._

  val file: File = new File("/Users/lobzison/code/src/github.com/lobzison/resources/test_output")

  val writer = new FileOutputStream(file)

  writer.write("govno".getBytes())
  writer.write("zalupa".getBytes())
  writer.write("sir".getBytes())
  writer.flush()
  writer.close()

}
