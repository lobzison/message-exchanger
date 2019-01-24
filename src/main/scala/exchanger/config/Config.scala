package exchanger.config

object Config {
  case class DbConfig(driverClassName: String, url: String, user: String, password: String)

  case class FileConfig(filePath: String)
}
