package object examples {
  type CommandHandler[Commands, State] = Commands => State => Either[String, State]
}

