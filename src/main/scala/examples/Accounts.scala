package examples

object Accounts {
  sealed trait Commands
  case class UserId(id: String)
  case class Register(userId: UserId) extends Commands
  case class UnRegister(userId: UserId) extends Commands
  case class Login(userId: UserId) extends Commands
  case class Logout(userId: UserId) extends Commands

  case class State(registeredUsers: Set[UserId], loggedUsers: Set[UserId])

  object State {
    def empty = State(Set.empty, Set.empty)
  }

  val commandHandler: CommandHandler[Commands, State] =
    command =>
      state =>
        command match {
          case Register(userId) if state.registeredUsers contains userId =>
            Left("Cannot register again, already registered")
          case Register(userId) =>
            Right apply state.copy(registeredUsers =
              state.registeredUsers + userId
            )
          case UnRegister(userId)
            if !(state.registeredUsers contains userId) =>
            Left("Cannot unregister user, user is not registered")
          case UnRegister(userId) =>
            Right apply state.copy(registeredUsers =
              state.registeredUsers - userId
            )
          case Login(userId) if !(state.registeredUsers contains userId) =>
            Left("Cannot login user, user is not registered")
          case Login(userId) if state.loggedUsers contains userId =>
            Left("Cannot login user, user is already logged in")
          case Login(userId) =>
            Right apply state.copy(loggedUsers = state.loggedUsers + userId)
          case Logout(userId) if !(state.registeredUsers contains userId) =>
            Left("Cannot logout user, user is not registered")
          case Logout(userId) if !(state.loggedUsers contains userId) =>
            Left("Cannot logout user, user is already logged out")
          case Logout(userId) =>
            Right apply state.copy(loggedUsers = state.loggedUsers + userId)
        }
}
