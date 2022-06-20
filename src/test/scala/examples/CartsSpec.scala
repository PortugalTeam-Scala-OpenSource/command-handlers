package examples

import examples.Carts._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class CartsSpec extends AnyFlatSpec {
  "Carts.State" should "add a product to a cart" in {

    val cartId = CartId("cart1")
    val product = ProductId("tvMDX")

    val cartsState = new Carts.State(Map(cartId -> Map.empty))

    cartsState.addProductToCart(cartId, product) shouldBe
      new Carts.State(cartsState.cartList + (cartId -> Map(product -> ProductQuantity(1))))
  }

  "Carts.State" should "remove product from a cart" in {

    val cartId = CartId("cart1")
    val product = ProductId("tvMDX")
    val productQuantity1 = ProductQuantity(2)

    val cartsState = new Carts.State(Map(cartId -> Map(product -> productQuantity1)))

    cartsState.removeProductFromCart(cartId, product) shouldBe
      new Carts.State(cartsState.cartList + (cartId -> Map.empty))
  }

  "Carts.State" should "reduce product quantity from cart" in {

    val cartId = CartId("cart1")
    val product = ProductId("tvMDX")
    val productQuantity1 = ProductQuantity(2)

    val cartsState = new Carts.State(Map(cartId -> Map(product -> productQuantity1)))

    cartsState.reduceProductQuantityInCart(cartId, product) shouldBe
      new Carts.State(cartsState.cartList + (cartId -> Map(product -> ProductQuantity(1))))
  }

  "Carts.State" should "increase product quantity from a cart" in {

    val cartId = CartId("cart1")
    val product = ProductId("tvMDX")
    val productQuantity1 = ProductQuantity(2)

    val cartsState = new Carts.State(Map(cartId -> Map(product -> productQuantity1)))

    cartsState.increaseProductQuantityInCart(cartId, product) shouldBe
      new Carts.State(cartsState.cartList + (cartId -> Map(product -> ProductQuantity(3))))
  }
}
