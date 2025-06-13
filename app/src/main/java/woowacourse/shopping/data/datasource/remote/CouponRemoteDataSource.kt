package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.response.CouponDto
import woowacourse.shopping.data.service.CouponApiService
import woowacourse.shopping.data.util.requireBody

class CouponRemoteDataSource(
    private val couponService: CouponApiService,
) {
    suspend fun getCoupons(): Result<List<CouponDto>> =
        runCatching {
            couponService.getCoupons().requireBody()
        }
}
