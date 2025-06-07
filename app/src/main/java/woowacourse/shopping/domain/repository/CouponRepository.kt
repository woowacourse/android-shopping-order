package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.exception.NetworkResult

interface CouponRepository {
    suspend fun getCoupons(): NetworkResult<List<Coupon>>
}
