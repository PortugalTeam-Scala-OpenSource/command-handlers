import examples.CommandHandler
import logging.Logger
import play.api.libs.json.{JsError, JsSuccess}
import serialization.json.JsonSerialization.Play.JsonSerialization
import serialization.{Deserializer, Serializer}

import scala.concurrent.{ExecutionContext, Future}

trait Actor[Commands, Event] {
  def process(c: Commands): Future[Either[String, Event]]
}

object Reader {
  case class Output(
      killswitch: Unit,
      done: Future[Unit]
  )
}
trait Reader[Command, Output] {
  def read(topic: String)(
      callback: Command => Output
  )(implicit
      deserializer: Deserializer[Command]
  ): Reader.Output
}
trait Writer[Events] {
  def write(topic: String)(implicit
      serializer: Serializer[Events]
  ): Events => Future[Unit]
}

case class Processor[Commands, Events](
    inTopic: String,
    outTopic: String,
    actor: Actor[Commands, Events]
)(implicit
    executionContext: ExecutionContext,
    reader: Reader[Commands, Future[Unit]],
    writer: Writer[Events],
    deserializer: Deserializer[Commands],
    serializer: Serializer[Events],
    logger: Logger
) {
  def start = {
    reader.read(inTopic) { command =>
      actor.process(command) flatMap {
        case Left(error) =>
          logger.error(error)
        case Right(event) =>
          writer.write(outTopic)(serializer)(
            event
          ) // TODO remove the explicit reference to the serializer
      }
    }
  }
}

trait Readside[Queries, Response] {
  def query: Queries => Future[Response]
}

object Example extends App {

  // this is a mock
  implicit def reader[Commands, Output](implicit
      commands: Seq[Commands]
  ) = new Reader[Commands, Output] {
    override def read(topic: String)(
        callback: Commands => Output
    )(implicit deserializer: Deserializer[Commands]): Reader.Output = {
      Reader.Output(
        killswitch = () => (),
        done = Future.successful(
          commands.foreach(callback)
        )
      )
    }
  }
  // this is a mock
  implicit def writer[Events]: Writer[Events] = new Writer[Events] {
    override def write(topic: String)(implicit
        serializer: Serializer[Events]
    ): Events => Future[Unit] =
      event => Future.successful(())
  }
  implicit val logger: Logger = new Logger {
    override def log(message: String): Future[Unit] = Future.successful(
      println(message)
    )

    override def error(message: String): Future[Unit] = log(message)
  }
  implicit val executionContext = scala.concurrent.ExecutionContext.global

  object Payment {
    sealed trait Commands
    case class Pay(money: Int) extends Commands
    case class Withdraw(money: Int) extends Commands
    sealed trait Events
    case class Paid(money: Int) extends Events
    case class Withdrew(money: Int) extends Events
    case class State(money: Int)
    object State { def empty = State(money = 0) }

    val commandHandler: CommandHandler[Commands, State] =
      command =>
        state =>
          command match {
            case Pay(money) =>
              Right apply state.copy(money = state.money + money)
            case Withdraw(money) if state.money - money >= 0 =>
              Right apply state.copy(money = state.money - money)
            case Withdraw(money) =>
              Left apply s"You cannout withdraw $money because you only have ${state.money}"
          }

    def toEvent: Commands => Events = {
      case Pay(money)      => Paid(money)
      case Withdraw(money) => Withdrew(money)
    }

    var state = State.empty

    case class GetState()
    val readside: Readside[GetState, State] = new Readside[GetState, State] {
      override def query: GetState => Future[State] = query =>
        Future.successful(state)
    }

    val paymentActor: Actor[Commands, Events] = new Actor[Commands, Events] {
      override def process(command: Commands): Future[Either[String, Events]] =
        Future.successful(
          commandHandler.apply(command)(state) match {
            case Left(error) => Left(error)
            case Right(newState) =>
              state = newState
              Right(toEvent(command))
          }
        )
    }

    import play.api.libs.json.{Json, Format}
    implicit val payF = Json.format[Pay]
    implicit val withdrawF = Json.format[Withdraw]
    implicit val commandsFormat = JsonSerialization(Json.format[Commands])

    implicit val paidF = Json.format[Paid]
    implicit val withdrewF = Json.format[Withdrew]
    implicit val eventsFormat = JsonSerialization(Json.format[Events])

    def processor(implicit dataset: Seq[Commands]) =
      Processor("paymentTopic", "paymentEventsTopic", Payment.paymentActor)
  }

  import Payment._
  implicit val commands = (1 to 10) map (index => Pay(money = 1))
  val processor = Payment.processor
  val readside = Payment.readside

  val output = processor.start

  for {
    state <- readside.query(GetState())
  } yield println(state)

}
