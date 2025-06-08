package woowacourse.shopping.data.coupon.source

import woowacourse.shopping.data.coupon.dto.CouponResponse

interface CouponDataSource {
    suspend fun coupons(): CouponResponse?
}