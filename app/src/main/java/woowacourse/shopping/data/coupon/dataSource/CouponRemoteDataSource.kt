package woowacourse.shopping.data.coupon.dataSource

import woowacourse.shopping.data.coupon.remote.dto.CouponResponseDto

interface CouponRemoteDataSource {
    suspend fun getCoupons(): List<CouponResponseDto>
}
