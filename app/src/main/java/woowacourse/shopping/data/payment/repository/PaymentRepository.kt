package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.coupon.dto.CouponResponse
import woowacourse.shopping.data.payment.CouponFetchResult

interface PaymentRepository {
    suspend fun fetchAllCoupons(): CouponFetchResult<CouponResponse>
}
