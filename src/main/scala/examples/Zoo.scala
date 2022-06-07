package examples

import scala.util.Try

object Zoo {
  trait Animal {
    val name: String
  }
  case class Cow(name: String) extends Animal
  case class Lion(name: String) extends Animal

  val animals = Seq(
    Cow("The fat cow"),
    Lion("The important lion")
  )

  object Visit {

    case class NoMoreAnimalsToVisit()
    case class NextAnimal(animal: Animal)

  }
  class Visit() {
    import Visit._
    val steps: Iterator[Animal] = animals.iterator

    def goNext: Either[NoMoreAnimalsToVisit, NextAnimal] = Try {
      steps.next()
    }.toEither match {
      case Left(exception) => Left(NoMoreAnimalsToVisit())
      case Right(animal)   => Right(NextAnimal(animal))
    }

  }
}
