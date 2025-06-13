package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.coupon.CouponResponse

interface CouponRemoteDataSource {
    suspend fun fetchCoupons(): List<CouponResponse>
}
