package examples

import examples.Products._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class ProductsSpec extends AnyFlatSpec {

  "Products.State" should "add a product to a list" in {
    val productsState = new Products.State(Map.empty)

    productsState.addProduct(ProductId("TvFVT25")) shouldBe
      new Products.State(productsState.productsList + (ProductId("TvFVT25") -> ProductQuantity(productQuantity = 0)))

    productsState.addProduct(ProductId("DesktopZW32")) shouldBe
      new Products.State(productsState.productsList + (ProductId("DesktopZW32") -> ProductQuantity(productQuantity = 0)))
  }

  "Products.State" should "remove a product from a list" in {
    val productsState = new Products.State(Map(ProductId("TvFVT25") -> ProductQuantity(35), ProductId("DesktopZW32") -> ProductQuantity(50)))

    productsState.removeProduct(ProductId("TvFVT25")) shouldBe
      new Products.State(productsState.productsList - ProductId("TvFVT25"))

    productsState.removeProduct(ProductId("DesktopZW32")) shouldBe
      new Products.State(productsState.productsList - ProductId("DesktopZW32"))
  }
}
