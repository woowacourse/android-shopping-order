package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.model.payment.Coupon
import woowacourse.shopping.error.Error
import woowacourse.shopping.error.Result

interface PaymentRepository {
    suspend fun getCoupons(): Result<List<Coupon>, Error>
}
