package woowacourse.shopping.data.source.coupon

import woowacourse.shopping.data.network.coupon.RetrofitCouponService
import woowacourse.shopping.data.network.coupon.dto.toDomain
import woowacourse.shopping.domain.coupon.Coupon

class CouponDataSourceImpl(
    private val couponService: RetrofitCouponService
) : CouponDataSource {
    override suspend fun loadCoupons(): List<Coupon> {
        val response = couponService.requestCoupons()
        check(response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            "coupons 요청 실패: ${response.code()},  Message: $errorBody"
        }
        val body = response.body() ?: error("empty body")
        return body.map { it.toDomain() }
    }
}
