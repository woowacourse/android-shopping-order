package woowacourse.shopping.data.network.coupon

import woowacourse.shopping.data.network.coupon.dto.toDomain
import woowacourse.shopping.domain.coupon.Coupon

class CouponDaoImpl(
    private val couponService: RetrofitCouponService
) : CouponDao {
    override suspend fun getCoupons(): List<Coupon> {
        val response = couponService.requestCoupons()
        check(response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            "coupons 요청 실패: ${response.code()},  Message: $errorBody"
        }
        val body = response.body() ?: error("empty body")
        return body.map { it.toDomain() }
    }
}
