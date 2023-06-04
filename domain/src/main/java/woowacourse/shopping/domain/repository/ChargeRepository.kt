package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.util.WoowaResult

interface ChargeRepository {
    fun fetchCharge(callback: (WoowaResult<Int>) -> Unit)
    fun recharge(amount: Int, callback: (WoowaResult<Int>) -> Unit)
}
