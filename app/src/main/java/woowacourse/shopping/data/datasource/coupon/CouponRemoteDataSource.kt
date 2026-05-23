package woowacourse.shopping.data.datasource.coupon

import woowacourse.shopping.data.network.coupon.CouponService
import woowacourse.shopping.domain.coupon.Coupon

class CouponRemoteDataSource(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun getAllCoupons(): List<Coupon> {
        val response = couponService.getAllCoupons()
        check(response.isSuccessful) { "coupons 요청 실패: ${response.code()}" }

        val body = response.body() ?: error("empty body")
        return body.mapNotNull { it.toDomain() }
    }
}
