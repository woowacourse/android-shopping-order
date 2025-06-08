package woowacourse.shopping.data.coupon.dataSource

import woowacourse.shopping.data.coupon.remote.dto.CouponResponseDto
import woowacourse.shopping.data.coupon.remote.service.CouponService

class DefaultCouponRemoteDataSource(
    private val couponService: CouponService,
) : CouponRemoteDataSource {
    override suspend fun getCoupons(): List<CouponResponseDto> = couponService.getCoupons()
}
