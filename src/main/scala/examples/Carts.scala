package examples

object Carts {

  case class CartId(id: String)
  case class ProductId(id: String)
  case class ProductQuantity(productQuantity: Int)

  sealed trait Commands
  case class AddProductToCart(cartId: CartId, productId: ProductId) extends Commands
  case class RemoveProductFromCart(cartId: CartId, productId: ProductId) extends Commands
  case class ReduceProductQuantityInCart(cartId: CartId, productId: ProductId) extends Commands
  case class IncreaseProductQuantityInCart(cartId: CartId, productId: ProductId) extends Commands

  case class State(cartList: Map[CartId, Map[ProductId, ProductQuantity]]){
    def addProductToCart(cartId: CartId, productId: ProductId): State = {
      val productMap = cartList(cartId)
      copy(cartList = cartList + (cartId -> (productMap + (productId -> ProductQuantity(1)))))
    }

    def removeProductFromCart(cartId: CartId, productId: ProductId): State = {
      val productMap = cartList(cartId)
      copy(cartList = cartList + (cartId -> (productMap - productId)))
    }

    def reduceProductQuantityInCart(cartId: CartId, productId: ProductId): State = {
      val productMap = cartList(cartId)
      val productQuantity = productMap(productId).productQuantity
      copy(cartList = cartList + (cartId -> (productMap + (productId -> ProductQuantity(productQuantity - 1)))))
    }

    def increaseProductQuantityInCart(cartId: CartId, productId: ProductId): State = {
      val productMap = cartList(cartId)
      val productQuantity = productMap(productId).productQuantity
      copy(cartList = cartList + (cartId -> (productMap + (productId -> ProductQuantity(productQuantity + 1)))))
    }
  }

  object State {
    def empty: State = State(Map.empty)
  }

  val commandHandler: CommandHandler[Commands, State] =
    commands =>
      state =>
        commands match {
          case AddProductToCart(cartId: CartId, productId: ProductId) if state.cartList(cartId).contains(productId) =>
            Right(state.increaseProductQuantityInCart(cartId, productId))
          case AddProductToCart(cartId: CartId, productId: ProductId) =>
            Right(state.addProductToCart(cartId, productId))
          case RemoveProductFromCart(cartId: CartId, productId: ProductId) if !state.cartList(cartId).contains(productId) =>
            Left("Product not found in cart")
          case RemoveProductFromCart(cartId: CartId, productId: ProductId) =>
            Right(state.removeProductFromCart(cartId, productId))
          case ReduceProductQuantityInCart(cartId: CartId, productId: ProductId)
            if !state.cartList(cartId).contains(productId) =>
            Left("Cant reduce quantity of a product that isn't in the cart")
          case ReduceProductQuantityInCart(cartId: CartId, productId: ProductId)
            if state.cartList(cartId)(productId).productQuantity == 1 =>
            Right(state.removeProductFromCart(cartId, productId))
          case ReduceProductQuantityInCart(cartId: CartId, productId: ProductId) =>
            Right(state.reduceProductQuantityInCart(cartId, productId))
          case IncreaseProductQuantityInCart(cartId: CartId, productId: ProductId)
            if !state.cartList(cartId).contains(productId) =>
            Left("Cant increase quantity of a product that isn't in the cart")
          case IncreaseProductQuantityInCart(cartId: CartId, productId: ProductId) =>
            Right(state.increaseProductQuantityInCart(cartId, productId))
        }
}
