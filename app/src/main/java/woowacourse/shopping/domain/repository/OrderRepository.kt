package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.domain.model.order.Coupon
import woowacourse.shopping.error.Error
import woowacourse.shopping.error.Result

interface OrderRepository {
    val coupons: StateFlow<List<Coupon>>

    suspend fun loadCoupons(): Result<Unit, Error>

    suspend fun orderCartItems(itemIds: List<Long>): Result<Unit, Error>
}
