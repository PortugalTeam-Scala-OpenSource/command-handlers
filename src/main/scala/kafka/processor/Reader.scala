package kafka.processor

import scala.concurrent.Future

object Reader {
  case class Output(
      killswitch: () => Unit,
      done: Future[Unit]
  )
}
