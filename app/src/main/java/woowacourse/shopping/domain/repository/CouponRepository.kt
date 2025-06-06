package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.Coupon

interface CouponRepository {
    fun fetchCoupons(): Result<List<Coupon>>
}
