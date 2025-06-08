package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.CouponFetchResult
import woowacourse.shopping.data.payment.dto.CouponResponse

interface CouponRemoteDataSource {
    suspend fun fetchCoupons(): CouponFetchResult<CouponResponse>
}
