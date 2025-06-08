package woowacourse.shopping.data.source.remote.coupon

import woowacourse.shopping.data.model.coupon.Coupon

interface CouponDataSource {
    suspend fun getCoupons(): Result<List<Coupon>>
}