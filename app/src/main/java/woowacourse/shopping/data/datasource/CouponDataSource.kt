package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.network.response.coupon.CouponResponse
import woowacourse.shopping.data.network.service.CouponService

class CouponDataSource(
    private val service: CouponService,
) {
    suspend fun getAll(): Result<CouponResponse> {
        return runCatching {
            service.getAll()
        }
    }
}
