package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.response.coupon.CouponResponseDto
import woowacourse.shopping.data.service.CouponApiService

class CouponRemoteDataSource(
    private val couponApiService: CouponApiService,
) {
    suspend fun getCoupons(): Result<List<CouponResponseDto>> {
        val response = couponApiService.getCoupons()

        return if (response.isSuccessful) {
            val body = response.body()
            Result.success(body ?: emptyList())
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }
}
