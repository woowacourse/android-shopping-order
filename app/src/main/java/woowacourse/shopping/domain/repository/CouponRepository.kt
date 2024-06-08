package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.response.Response

interface CouponRepository {
    suspend fun allCoupons(): List<Coupon>

    suspend fun allCouponsResponse():Response<List<Coupon>>
}
