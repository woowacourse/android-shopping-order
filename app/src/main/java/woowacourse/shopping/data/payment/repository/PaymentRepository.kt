package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.CouponFetchResult
import woowacourse.shopping.data.payment.OrderRequestResult
import woowacourse.shopping.data.payment.dto.CouponResponse

interface PaymentRepository {
    suspend fun fetchAllCoupons(): CouponFetchResult<CouponResponse>

    suspend fun requestOrder(orderCartIds: List<Int>): OrderRequestResult<Int>
}
