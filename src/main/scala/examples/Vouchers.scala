package examples

object Vouchers extends App {
 
  case class VoucherId(id: String)
  sealed trait Commands 
  case class AddVoucher(voucherId: VoucherId) extends Commands
  case class RemoveVoucher(voucherId: VoucherId) extends Commands
  
  case class State(vouchers: Set[VoucherId]) {
    def addVoucher(voucherId: VoucherId) = copy(vouchers = vouchers + voucherId)
    def removeVoucher(voucherId: VoucherId) = copy(vouchers = vouchers - voucherId)
  }

  object State {
    def empty: State = State(vouchers = Set.empty)
  }
  
  val commandHandler: CommandHandler[Commands, State] = 
    commands => 
    state => 
    commands match {
      case AddVoucher(voucherId) if state.vouchers.contains(voucherId) => Left("Cannot add voucher if already added")
      case AddVoucher(voucherId) => Right(state.addVoucher(voucherId))
      case RemoveVoucher(voucherId) if !state.vouchers.contains(voucherId) => Left("Voucher not found")
      case RemoveVoucher(voucherId)  => Right(state.removeVoucher(voucherId))
    }
}
