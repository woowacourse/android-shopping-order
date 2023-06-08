package woowacourse.shopping.domain.repository

interface ChargeRepository {
    fun fetchCharge(callback: (Result<Int>) -> Unit)
    fun recharge(amount: Int, callback: (Result<Int>) -> Unit)
}
