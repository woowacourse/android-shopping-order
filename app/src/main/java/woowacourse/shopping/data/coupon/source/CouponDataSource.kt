package woowacourse.shopping.data.coupon.source

import woowacourse.shopping.data.coupon.dto.CouponResponseItem

interface CouponDataSource {
    suspend fun coupons(): List<CouponResponseItem>
}