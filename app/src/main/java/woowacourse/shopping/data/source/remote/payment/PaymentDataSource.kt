package woowacourse.shopping.data.source.remote.payment

import woowacourse.shopping.data.model.CouponResponse

interface PaymentDataSource {
    suspend fun getCoupons(): Result<List<CouponResponse>>
}