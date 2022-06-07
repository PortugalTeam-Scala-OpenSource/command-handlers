package object examples {
  type CommandHandler[Commands, State] = Command => State => Either[String, State]
}

