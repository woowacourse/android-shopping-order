package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.result.Result

interface CouponRepository {
    suspend fun allCoupons(): List<Coupon>

    suspend fun allCouponsResponse(): Result<List<Coupon>>
}
