package examples

object Reviews {

  case class ReviewId(id: String)
  case class ProductId(id: String)
  case class UserId(id: String)
  case class Comment(comment: String)
  case class Rate(rate: Int)

  sealed trait Commands
  case class AddReview(reviewId: ReviewId, productId: ProductId, userId: UserId, comment: Comment, rate: Rate) extends Commands
  case class RemoveReview(reviewId: ReviewId) extends Commands
  case class EditReview(reviewId: ReviewId, productId: ProductId, userId: UserId, comment: Comment, rate: Rate) extends Commands

  case class State(reviewList: Map[ReviewId, Map[ProductId, Map[UserId, Map[Comment, Rate]]]]){
    def addReview(reviewId: ReviewId, productId: ProductId, userId: UserId, comment: Comment, rate: Rate): State = {
      val reviewInfoMap = reviewList(reviewId)
      val productInfoMap = reviewInfoMap(productId)
      val userInfoMap = productInfoMap(userId)

      copy(reviewList = reviewList + (reviewId -> (reviewInfoMap + (productId -> (productInfoMap + (userId -> (userInfoMap + (comment -> rate))))))))
    }

    def removeReview(reviewId: ReviewId): State =
      copy(reviewList = reviewList.removed(reviewId))
  }

  object State {
    def empty: State = State(reviewList = Map.empty)
  }

  val commandHandler: CommandHandler[Commands, State] =
    commands =>
      state =>
        commands match {
          case AddReview(reviewId: ReviewId, productId: ProductId, userId: UserId, comment: Comment, rate: Rate)
            if state.reviewList.contains(reviewId) => Left("This Review was already added")
          case AddReview(reviewId: ReviewId, productId: ProductId, userId: UserId, comment: Comment, rate: Rate) =>
            Right(state.addReview(reviewId, productId, userId, comment, rate))
          case RemoveReview(reviewId: ReviewId)
            if !state.reviewList.contains(reviewId) => Left("Review doesn't exist")
          case RemoveReview(reviewId: ReviewId) => Right(state.removeReview(reviewId))
          case EditReview(reviewId: ReviewId, productId: ProductId, userId: UserId, comment: Comment, rate: Rate)
            if !state.reviewList.contains(reviewId) => Left("Review doesn't exist")
          case EditReview(reviewId: ReviewId, productId: ProductId, userId: UserId, comment: Comment, rate: Rate) =>
            Right(state.addReview(reviewId, productId, userId, comment, rate))
        }
}
