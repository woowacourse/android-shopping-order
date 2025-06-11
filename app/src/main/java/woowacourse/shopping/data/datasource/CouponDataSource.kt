package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.order.Coupon

interface CouponDataSource {
    suspend fun fetchCoupons(): List<Coupon>
}
