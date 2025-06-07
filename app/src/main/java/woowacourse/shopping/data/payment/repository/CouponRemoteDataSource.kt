package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.coupon.dto.CouponResponse
import woowacourse.shopping.data.payment.CouponFetchResult

interface CouponRemoteDataSource {
    suspend fun fetchCoupons(): CouponFetchResult<CouponResponse>
}
