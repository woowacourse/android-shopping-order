package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.coupon.CouponResponse

interface CouponDataSource {
    suspend fun fetchCoupon(): CouponResponse
}
