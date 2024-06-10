package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.CouponData

interface CouponDataSource {
    suspend fun couponsData(): Result<List<CouponData>>

    suspend fun coupon(couponId: Long): Result<CouponData>
}