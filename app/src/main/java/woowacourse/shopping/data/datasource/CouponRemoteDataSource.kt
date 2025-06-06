package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.coupon.CouponResponse

interface CouponRemoteDataSource {
    fun fetchCoupons(): Result<List<CouponResponse>>
}
