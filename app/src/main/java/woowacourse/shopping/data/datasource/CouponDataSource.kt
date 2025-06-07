package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.order.Coupons

interface CouponDataSource {
    suspend fun fetchCoupons(): Coupons
}
