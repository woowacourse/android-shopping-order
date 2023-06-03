package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.util.WoowaResult

interface ChargeRepository {
    fun fetchCharge(callback: (WoowaResult<Long>) -> Unit)
    fun recharge(amount: Long, callback: (WoowaResult<Long>) -> Unit)
}
