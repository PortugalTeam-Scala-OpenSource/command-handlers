package logging

import scala.concurrent.Future

trait Logger {
  def log(message: String): Future[Unit]
  def error(message: String): Future[Unit]
}
