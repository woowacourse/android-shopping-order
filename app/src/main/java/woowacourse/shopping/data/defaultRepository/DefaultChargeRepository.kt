package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.domain.repository.ChargeRepository
import woowacourse.shopping.domain.util.WoowaResult

class DefaultChargeRepository : ChargeRepository {
    override fun fetchCharge(callback: (WoowaResult<Long>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun recharge(amount: Long, callback: (WoowaResult<Long>) -> Unit) {
        TODO("Not yet implemented")
    }
}
