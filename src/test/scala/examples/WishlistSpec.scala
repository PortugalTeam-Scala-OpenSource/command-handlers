package examples

import examples.Wishlist._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class WishlistSpec extends AnyFlatSpec {
  "Wishlist.State" should "add a product to a wishlist" in {

    val userId = UserId("user1")
    val product = ProductId("tvMDX")

    val wishlistState = new Wishlist.State(Map(userId -> Set.empty))

    wishlistState.addToWishList(userId, product) shouldBe
      new Wishlist.State(wishlistState.wishlist + (userId -> Set(product)))
  }

  "Wishlist.State" should "remove product from a wishlist" in {

    val userId = UserId("user1")
    val product = ProductId("tvMDX")
    val product2 = ProductId("SamsungA20")

    val wishlistState = new Wishlist.State(Map(userId -> Set(product, product2)))
    val wishlistState2 = new Wishlist.State(Map(userId -> Set(product2)))

    wishlistState.removeFromWishList(userId, product) shouldBe
      new Wishlist.State(wishlistState.wishlist + (userId -> Set(product2)))

    wishlistState2.removeFromWishList(userId, product2) shouldBe
      new Wishlist.State(Map(userId -> Set.empty))
  }
}
