package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.coupon.dto.CouponResponse
import woowacourse.shopping.data.payment.CouponFetchResult
import woowacourse.shopping.data.payment.OrderRequestResult

interface PaymentRepository {
    suspend fun fetchAllCoupons(): CouponFetchResult<CouponResponse>

    suspend fun requestOrder(orderCartIds: List<Int>): OrderRequestResult<Int>
}
