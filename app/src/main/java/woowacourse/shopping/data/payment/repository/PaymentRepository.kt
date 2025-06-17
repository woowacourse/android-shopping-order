package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.dto.CouponListResponse
import woowacourse.shopping.data.util.api.ApiResult

interface PaymentRepository {
    suspend fun fetchAllCoupons(): ApiResult<CouponListResponse>

    suspend fun requestOrder(orderCartIds: List<Int>): ApiResult<Int>
}
