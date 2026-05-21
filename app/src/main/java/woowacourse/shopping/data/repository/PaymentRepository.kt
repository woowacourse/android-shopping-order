package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Coupon

interface PaymentRepository {
    suspend fun getCoupons(): List<Coupon>
}
