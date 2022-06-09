package examples

object Products {

  case class ProductId(productId: String)
  case class ProductQuantity(productQuantity: Int)

  sealed trait Commands
  case class AddProduct(productId: ProductId) extends Commands
  case class RemoveProduct(productId: ProductId) extends Commands
  case class TakeNProducts(productId: ProductId, n: ProductQuantity) extends Commands
  case class PutNProducts(productId: ProductId, n: ProductQuantity) extends Commands

  case class State(productsList: Map[ProductId, ProductQuantity]){
    def addProduct(productId: ProductId): State =
      copy(productsList = productsList + (productId, ProductQuantity(productQuantity = 0)))
    def removeProduct(productId: ProductId): State =
      copy(productsList = productsList - productId)
    def takeNProducts(productId: ProductId,  n: ProductQuantity): State
    = copy(productsList = productsList + (productId, ProductQuantity(productsList(productId).productQuantity - n.productQuantity)))
    def putNProducts(productId: ProductId, n: ProductQuantity): State
    = copy(productsList = productsList + (productId, ProductQuantity(productsList(productId).productQuantity + n.productQuantity)))
  }

  object State {
    def empty: State = State(productsList = Map.empty)
  }

  val commandHandler: CommandHandler[Commands, State] =
    commands =>
      state =>
        commands match {
          case AddProduct(productId) if state.productsList.contains(productId) => Left("The Product was already added")
          case AddProduct(productId) => Right(state.addProduct(productId))
          case RemoveProduct(productId) if !state.productsList.contains(productId) => Left("Product not found")
          case RemoveProduct(productId) => Right(state.removeProduct(productId))
          case TakeNProducts(productId, n)
            if state.productsList(productId).productQuantity < n.productQuantity => Left("Cant take that manny")
          case TakeNProducts(productId, n) => Right(state.takeNProducts(productId, n))
          case PutNProducts(productId, n) => Right(state.putNProducts(productId, n))
        }
}
