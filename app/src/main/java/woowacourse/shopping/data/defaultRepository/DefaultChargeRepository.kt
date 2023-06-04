package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.request.RequestChargeDto
import woowacourse.shopping.domain.repository.ChargeRepository
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.util.enqueueUtil

class DefaultChargeRepository : ChargeRepository {
    override fun fetchCharge(callback: (WoowaResult<Int>) -> Unit) {
        ServicePool.retrofitService.getCharge().enqueueUtil(
            onSuccess = { callback(WoowaResult.SUCCESS(it.totalCash)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }

    override fun recharge(amount: Int, callback: (WoowaResult<Int>) -> Unit) {
        ServicePool.retrofitService.recharge(RequestChargeDto(amount)).enqueueUtil(
            onSuccess = { callback(WoowaResult.SUCCESS(it.totalCash)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }
}
