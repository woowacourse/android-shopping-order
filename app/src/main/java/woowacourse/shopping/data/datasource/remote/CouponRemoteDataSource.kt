package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.coupon.CouponResponse

interface CouponRemoteDataSource {
    suspend fun fetchCoupons(): Result<List<CouponResponse>>
}
