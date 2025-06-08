package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.response.coupon.CouponResponseDto
import woowacourse.shopping.data.service.CouponApiService

class CouponRemoteDataSource(
    private val couponApiService: CouponApiService,
) {
    suspend fun getCoupons(): Result<List<CouponResponseDto>> {
        return couponApiService.getCoupons()
    }
}
