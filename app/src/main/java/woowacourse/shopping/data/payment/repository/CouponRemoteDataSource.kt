package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.dto.CouponListResponse
import woowacourse.shopping.data.util.api.ApiResult

interface CouponRemoteDataSource {
    suspend fun fetchCoupons(): ApiResult<CouponListResponse>
}
