//import cats.effect.{ExitCode, IO, IOApp}
import examples.{CommandHandler, Hello}

object Main /*extends IOApp {

  object Payments {

    sealed trait Commands
    case class PaymentAmount(amount: Int) {
      def increase(by: Int) = copy(amount + by)
      def decrease(by: Int) = copy(amount - by)
    }
    case class CollectPayment(paymentAmount: PaymentAmount) extends Commands
    case class RefundPayment(paymentAmount: PaymentAmount) extends Commands

    case class State(paymentAmount: PaymentAmount) {
      def collect: CollectPayment => State = {
        case CollectPayment(PaymentAmount(amount)) =>
          copy(paymentAmount =
            paymentAmount.copy(amount = paymentAmount.amount + amount)
          )
      }
    }
    object State {
      def empty = State(PaymentAmount(0))
    }

    val commandHandler: CommandHandler[Commands, State] =
      command =>
        state =>
          command match {
            case CollectPayment(PaymentAmount(amount)) if amount > 0 =>
              Right apply state.copy(paymentAmount =
                state.paymentAmount.increase(amount)
              )
            case CollectPayment(PaymentAmount(amount)) =>
              Left apply "Amount should be positive"
            case RefundPayment(PaymentAmount(amount)) if amount > 0 =>
              Right apply state.copy(paymentAmount =
                state.paymentAmount.decrease(amount)
              )
            case RefundPayment(PaymentAmount(amount)) =>
              Left apply "Amount should be positive"
          }
  }

  object PaymentActorMock {
    var state = Payments.State.empty

  }
  import Payments._
  def collectPayments(value: Int): IO[Unit] =
    commandHandler(CollectPayment(PaymentAmount(value)))(
      PaymentActorMock.state
    ) match {
      case Left(value) =>
        println(s"[DEBUG] collectPayments error ${value}")
        IO.raiseError(new Exception(value))
      case Right(value) =>
        println(s"[DEBUG] collectPayments success ${value}")
        PaymentActorMock.state = value
        IO.pure(())
    }
  def refundPayments(value: Int): IO[Unit] =
    commandHandler(RefundPayment(PaymentAmount(value)))(
      PaymentActorMock.state
    ) match {
      case Left(value) =>
        println(s"[DEBUG] refundPayments error ${value}")
        IO.raiseError(new Exception(value))
      case Right(value) =>
        println(s"[DEBUG] refundPayments success ${value}")
        PaymentActorMock.state = value
        IO.pure(())
    }

  override def run(args: List[String]): IO[ExitCode] = {
    import com.vladkopanev.cats.saga.Saga._

    object Sage {
      type Op = () => IO[Unit]
      type AA = Seq[(Op, Op)]
      val a: AA = Seq(
        (() => collectPayments(1), () => refundPayments(1))
      )

      import cats.implicits._
      import cats.syntax._
      def transact(aa: AA): IO[Unit] = {
        def execute(todo: Op, toFix: Seq[Op]): IO[Unit] =
          todo().attempt.flatMap {
            case Left(value) =>
              val a: Seq[IO[Unit]] = toFix.map(_())
              a.head
            case Right(value) =>
              IO pure value
          }

        val aaa: Seq[(Sage.Op, Seq[Sage.Op])] =
          aa.foldLeft(Seq(aa.head._1 -> Seq(aa.head._2))) { case (acc, opop) =>
            val b: Seq[Sage.Op] = acc.flatMap { case (op, value: Seq[Op]) =>
              value :+ opop._2
            }
            acc :+ (opop._1 -> b)
          }
        aaa.map(opop => execute(opop._1, opop._2))
      }
    }

    (for {
      _ <- collectPayments(2).compensate[Throwable] { error =>
        println("HERE 1")
        if (error.isLeft) refundPayments(2)
        else IO.pure(())
      }
      _ <- collectPayments(-4).compensate[Throwable] { error =>
        println("HERE 2")
        if (error.isLeft) refundPayments(4)
        else IO.pure(())
      }
    } yield ()).transact.attempt.map {
      case Left(value) =>
        println(value)
        println(PaymentActorMock.state.paymentAmount)
        ExitCode(1)
      case Right(value) =>
        println(value)
        println(PaymentActorMock.state.paymentAmount)
        ExitCode(0)
    }
  }
}
 */
