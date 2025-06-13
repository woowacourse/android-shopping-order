package woowacourse.shopping.data.source.remote.coupon

import woowacourse.shopping.data.model.coupon.CouponResponse

interface CouponDataSource {
    suspend fun getCoupons(): Result<List<CouponResponse>>
}
