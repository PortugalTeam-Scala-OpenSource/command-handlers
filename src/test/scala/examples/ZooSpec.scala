package examples

import examples.Zoo._
import examples.Zoo.Visit
import examples.Zoo.Visit._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class ZooSpec extends AnyFlatSpec {

  "Zoo.Visit" should "visit animals one by one until there are no more animals to visit" in {
    val newVisit = new Visit()
    newVisit.goNext shouldBe Right(NextAnimal(Cow("The fat cow")))
    newVisit.goNext shouldBe Right(NextAnimal(Lion("The important lion")))
    newVisit.goNext shouldBe Left(NoMoreAnimalsToVisit())

    val secondVisit = new Visit()
    secondVisit.goNext shouldBe Right(NextAnimal(Cow("The fat cow")))
    secondVisit.goNext shouldBe Right(NextAnimal(Lion("The important lion")))
    secondVisit.goNext shouldBe Left(NoMoreAnimalsToVisit())
  }
}
