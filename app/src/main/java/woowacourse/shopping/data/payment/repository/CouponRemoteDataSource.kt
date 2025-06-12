package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.CouponFetchResult
import woowacourse.shopping.data.payment.dto.CouponListResponse

interface CouponRemoteDataSource {
    suspend fun fetchCoupons(): CouponFetchResult<CouponListResponse>
}
