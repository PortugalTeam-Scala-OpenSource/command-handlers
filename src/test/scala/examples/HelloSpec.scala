package examples

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import examples.Hello

class HelloSpec extends AnyFlatSpec {

  "Hello.sayHello" should "say hello" in {
    val name = "John"
    Hello.sayHello(name) shouldBe s"Hello $name"
  }
}
