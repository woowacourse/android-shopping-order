package woowacourse.shopping.data.coupon.remote.repository

import woowacourse.shopping.domain.coupon.Coupon

interface CouponRepository {
    fun getAllCoupons(): List<Coupon>
}
