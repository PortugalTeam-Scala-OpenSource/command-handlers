package examples

/*
This is a library.
We write object in Scala when we want to store functions that we are going to use
In this case, the library name is Hello,
and the function takes a String and returns a String, adding "Hello"
 */
object Hello {
  def sayHello(name: String): String = s"Hello $name"
}
