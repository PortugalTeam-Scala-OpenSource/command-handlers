package examples

object Session {
  case class Email(email: String)
  case class Password(password: String)
  case class Credentials(email: Email, password: Password)

  sealed trait Commands
  case class Login(credentials: Credentials) extends Commands
  case class Logout(credentials: Credentials) extends Commands

  case class State(sessionsList: Set[Credentials]){
    def login(credentials: Credentials) = copy(sessionsList = sessionsList + credentials)
    def logout(credentials: Credentials) = copy(sessionsList = sessionsList - credentials)
  }

  val commandHandler: CommandHandler[Commands, State] =
    commands =>
      state =>
        commands match {
          case Login(Credentials(email: Email, password: Password)) => Right(state.login(Credentials(email, password)))
          case Logout(Credentials(email, password)) => Right(state.logout(Credentials(email, password)))
        }
}
