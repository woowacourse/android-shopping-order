package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.model.payment.Coupon
import woowacourse.shopping.error.Error
import woowacourse.shopping.error.Result

interface OrderRepository {
    suspend fun getCoupons(): Result<List<Coupon>, Error>

    suspend fun orderCartItems(itemIds: List<Long>): Result<Unit, Error>
}
