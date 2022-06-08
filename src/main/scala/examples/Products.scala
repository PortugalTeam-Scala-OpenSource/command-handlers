package examples

object Products {

  case class Product(productId: String, productQuantity: Int)

  sealed trait Commands
  case class AddProduct(product: Product) extends Commands
  case class RemoveProduct(product: Product) extends Commands
/*  case class TakeNProducts(product: Product, n: Int) extends Commands
  case class PutNProducts(product: Product, n: Int) extends Commands*/

  case class State(productsList: Set[Product]){
    def addProduct(product: Product) = copy(productsList = productsList + product)
    def removeProduct(product: Product) = copy(productsList = productsList - product)
/*    def takeNProducts(product: Product) = copy(productsList)
    def putNProducts(product: Product) = copy(productsList)*/
  }

  object State {
    def empty: State = State(productsList = Set.empty)
  }

  val commandHandler: CommandHandler[Commands, State] =
    commands =>
      state =>
        commands match {
          case AddProduct(Product(productId, productQuantity)) =>
            if (state.productsList.contains(Product(productId, productQuantity))) Left("The Product was already added")
            else Right(state.addProduct(Product(productId, productQuantity)))

          case RemoveProduct(Product(productId, productQuantity)) =>
            if (!state.productsList.contains(Product(productId, productQuantity))) Left("Product not found")
            else Right(state.removeProduct(Product(productId, productQuantity)))

          /*case TakeNProducts(Product(productId, productQuantity), n) =>
            if (!state.productsList.contains(Product(productId, productQuantity))) Left("Product not found")
            else Right(state.takeNProducts(Product(productId, productQuantity = productQuantity - n)))

          case PutNProducts(Product(productId, productQuantity), n) =>
            if (!state.productsList.contains(Product(productId, productQuantity))) Left("Product not found")
            else Right(state.putNProducts(Product(productId, productQuantity = productQuantity + n)))*/
        }
}
