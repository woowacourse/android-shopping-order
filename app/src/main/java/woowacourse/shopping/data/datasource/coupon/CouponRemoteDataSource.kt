package woowacourse.shopping.data.datasource.coupon

import woowacourse.shopping.data.remote.dto.CouponResponseDto

interface CouponRemoteDataSource {
    suspend fun getCoupons(): List<CouponResponseDto>
}
