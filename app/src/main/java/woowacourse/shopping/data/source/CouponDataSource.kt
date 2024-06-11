package woowacourse.shopping.data.source

import woowacourse.shopping.domain.model.coupon.Coupon

interface CouponDataSource {
    suspend fun loadCoupons(): Result<List<Coupon>>
}
