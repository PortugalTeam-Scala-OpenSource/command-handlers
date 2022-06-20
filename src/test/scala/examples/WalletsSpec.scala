package examples

import examples.Wallets._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class WalletsSpec extends AnyFlatSpec {
  "Wallets.State" should "deposit currency to a wallet" in {
    val walletsState = new Wallets.State(Map(WalletId("wallet1") -> Balance(0)))

    walletsState.depositCurrency(WalletId("wallet1"), 150) shouldBe
      new Wallets.State(walletsState.wallets + (WalletId("wallet1") -> Balance(150)))
  }

  "Wallets.State" should "withdraw currency from a wallet" in {
    val walletsState = new Wallets.State(Map(WalletId("wallet1") -> Balance(200)))

    walletsState.withdrawCurrency(WalletId("wallet1"), 50) shouldBe
      new Wallets.State(walletsState.wallets + (WalletId("wallet1") -> Balance(150)))

    Wallets.State(walletsState.wallets + (WalletId("wallet1") -> Balance(150))).withdrawCurrency(WalletId("wallet1"), 100) shouldBe
      new Wallets.State(Map(WalletId("wallet1") -> Balance(50)))
  }
}
