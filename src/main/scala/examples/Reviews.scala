package examples

object Reviews {

  case class ReviewId(id: String)
  case class UserId(id: String)
  case class Comment(comment: String)
  case class Rate(rate: Int)

  sealed trait Commands
  case class AddReview(reviewId: ReviewId, userId: UserId, comment: Comment, rate: Rate) extends Commands
  case class RemoveReview(reviewId: ReviewId) extends Commands
  case class EditReview(reviewId: ReviewId, userId: UserId, comment: Comment, rate: Rate) extends Commands

  case class State(reviewList: Map[ReviewId, Map[UserId, Map[Comment, Rate]]]){
    def addReview(reviewId: ReviewId, userId: UserId, comment: Comment, rate: Rate): State =
      copy(reviewList = reviewList + (reviewId, Map[userId, Map[comment, rate]]))
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
          case AddReview(reviewId: ReviewId, userId: UserId, comment: Comment, rate: Rate)
            if state.reviewList.contains(reviewId) => Left("This Review was already added")
          case AddReview(reviewId: ReviewId, userId: UserId, comment: Comment, rate: Rate) =>
            Right(state.addReview(reviewId, userId, comment, rate))
          case RemoveReview(reviewId: ReviewId)
            if !state.reviewList.contains(reviewId) => Left("Review doesn't exist")
          case RemoveReview(reviewId: ReviewId) => Right(state.removeReview(reviewId))
          case EditReview(reviewId: ReviewId, userId: UserId, comment: Comment, rate: Rate)
            if !state.reviewList.contains(reviewId) => Left("Review doesn't exist")
          case EditReview(reviewId: ReviewId, userId: UserId, comment: Comment, rate: Rate) =>
            Right(state.addReview(reviewId, userId, comment, rate))
        }
}
