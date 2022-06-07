package examples

object Products {

  case class Product(productId: String, productQuantity: Int)

  sealed trait Commands
  case class AddProduct(product: Product) extends Commands
  case class RemoveProduct(product: Product) extends Commands
  case class TakeNProducts(productId: Product) extends Commands
  case class PutNProducts(productId: Product) extends Commands
  case class CheckIfExists(productId: Product) extends Commands
  case class CheckNumberInStock(productId: Product) extends Commands

  case class State(productsList: Set[Product]){
    def addProduct(product: Product) = copy(productsList = productsList + product)
    def removeProduct(product: Product) = copy(productsList = productsList - product)
  }

  object State {
    def empty: State = State(productsList = Set.empty)
  }

  val commandHandler: CommandHandler[Commands, State] =
    commands =>
      state =>
        commands match {
          case AddProduct(Product(productId, productQuantity)) => Right(state.addProduct(Product(productId, productQuantity)))
          case RemoveProduct(Product(productId, productQuantity))  => Right(state.removeProduct(Product(productId, productQuantity)))
        }
}
