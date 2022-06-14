package examples

object Wishlist {

  case class UserId(id: String)
  case class ProductId(id: String)
  case class Availability(availability: Boolean)

  sealed trait Commands
  case class AddToWishList(userId: UserId, product: Product) extends Commands
  case class RemoveFromWishList(userId: UserId, product: Product) extends Commands

  case class State(wishlist: Map[UserId, Map[ProductId, Availability]]){
    def addToWishList(userId: UserId, productId: ProductId): State = {
      val productList = wishlist(userId)
      copy(wishlist = wishlist + (userId, productList + productId))
    }
    def removeFromWishList(userId: UserId, productId: ProductId): State = {
      val productList = wishlist(userId)
      copy(wishlist = wishlist + (userId, productList - productId))
    }
  }

  object State {
    def empty: State = State(wishlist = Map.empty)
  }

  val commandHandler: CommandHandler[Commands, State] =
    commands =>
      state =>
        commands match {
          case AddToWishList(userId: UserId, productId: ProductId)
            if state.wishlist(userId).contains(productId) =>
            Left("This product was already added to wishList")
          case AddToWishList(userId: UserId, productId: ProductId) =>
            Right(state.addToWishList(userId, productId))
          case RemoveFromWishList(userId: UserId, productId: ProductId)
            if !state.wishlist(userId).contains(productId) =>
            Left("This product doesn't exist in the wishlist")
          case RemoveFromWishList(userId: UserId, productId: ProductId) =>
            Right(state.removeFromWishList(userId, productId))
        }
}
