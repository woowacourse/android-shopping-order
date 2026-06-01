package woowacourse.shopping.data.repository

import woowacourse.shopping.model.coupon.Coupon

interface PaymentRepository {
    suspend fun getCoupons(): List<Coupon>
}
