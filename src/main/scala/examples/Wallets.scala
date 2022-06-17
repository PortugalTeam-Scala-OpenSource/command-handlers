package examples

object Wallets {

  case class WalletId(id: String)
  case class Balance(decimal: BigDecimal)

  sealed trait Commands
  case class DepositCurrency(walletId: WalletId, currency: BigDecimal) extends Commands
  case class WithdrawCurrency(walletId: WalletId, currency: BigDecimal) extends Commands

  case class State(wallets: Map[WalletId, Balance]) {
    def depositCurrency(walletId: WalletId, currency: BigDecimal): State = {
      val currentBalance = wallets(walletId).decimal
      copy(wallets = wallets + (walletId -> Balance(currentBalance + currency)))
    }

    def withdrawCurrency(walletId: WalletId, currency: BigDecimal): State = {
      val currentBalance = wallets(walletId).decimal
      copy(wallets = wallets + (walletId -> Balance(currentBalance - currency)))
    }
  }

  object State {
    def empty: State = State(wallets = Map.empty)
  }

  val commandHandler: CommandHandler[Commands, State] =
    commands =>
      state =>
        commands match {
          case DepositCurrency(walletId: WalletId, currency : BigDecimal) if !state.wallets.contains(walletId) =>
            Left("Cant add currency to a wallet that doesn't exist")
          case DepositCurrency(walletId: WalletId, currency : BigDecimal) =>
            Right(state.depositCurrency(walletId, currency))
          case WithdrawCurrency(walletId: WalletId, currency: BigDecimal) if !state.wallets.contains(walletId) =>
            Left("Cant add currency to a wallet that doesn't exist")
          case WithdrawCurrency(walletId: WalletId, currency: BigDecimal) if state.wallets(walletId).decimal < currency =>
            Left("Wallet's balance is not enough")
          case WithdrawCurrency(walletId: WalletId, currency: BigDecimal)  =>
            Right(state.withdrawCurrency(walletId, currency))
        }
}
