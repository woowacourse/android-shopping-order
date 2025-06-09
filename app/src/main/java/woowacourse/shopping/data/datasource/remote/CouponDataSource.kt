package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.coupon.CouponResponse

interface CouponDataSource {
    suspend fun getCoupons(): List<CouponResponse>
}
