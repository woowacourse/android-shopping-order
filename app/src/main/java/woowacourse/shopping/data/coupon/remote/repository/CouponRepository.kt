package woowacourse.shopping.data.coupon.remote.repository

import woowacourse.shopping.domain.coupon.Coupon

interface CouponRepository {
    suspend fun getAllCoupons(): List<Coupon>
}
