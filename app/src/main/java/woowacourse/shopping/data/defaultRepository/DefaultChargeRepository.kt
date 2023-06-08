package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.request.RequestChargeDto
import woowacourse.shopping.domain.repository.ChargeRepository
import woowacourse.shopping.util.fetchResponseBody

class DefaultChargeRepository : ChargeRepository {
    override fun fetchCharge(callback: (Result<Int>) -> Unit) {
        ServicePool.retrofitService.getCharge().fetchResponseBody(
            onSuccess = { callback(Result.success(it.totalCash)) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun recharge(amount: Int, callback: (Result<Int>) -> Unit) {
        ServicePool.retrofitService.recharge(RequestChargeDto(amount)).fetchResponseBody(
            onSuccess = { callback(Result.success(it.totalCash)) },
            onFailure = { callback(Result.failure(it)) },
        )
    }
}
