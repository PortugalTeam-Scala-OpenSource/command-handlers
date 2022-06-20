package examples

import examples.Vouchers._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class VouchersSpec extends AnyFlatSpec {
  "Vouchers.State" should "add a voucher to a list" in {
    val vouchersState = new Vouchers.State(Set.empty)

    vouchersState.addVoucher(VoucherId("Promo2022-1")) shouldBe
      new Vouchers.State(vouchersState.vouchers + VoucherId("Promo2022-1"))

    vouchersState.addVoucher(VoucherId("Promo2022-2")) shouldBe
      new Vouchers.State(vouchersState.vouchers + VoucherId("Promo2022-2"))
  }

  "Vouchers.State" should "remove a voucher from a list" in {
    val vouchersState = new Vouchers.State(Set(VoucherId("Promo2021-1"), VoucherId("Promo2021-2")))

    vouchersState.removeVoucher(VoucherId("Promo2021-1")) shouldBe
      new Vouchers.State(vouchersState.vouchers - VoucherId("Promo2021-1"))

    vouchersState.removeVoucher(VoucherId("Promo2021-2")) shouldBe
      new Vouchers.State(vouchersState.vouchers - VoucherId("Promo2021-2"))
  }
}
