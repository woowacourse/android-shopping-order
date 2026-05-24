package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.domain.model.payment.Coupon
import woowacourse.shopping.error.Error
import woowacourse.shopping.error.Result

interface OrderRepository {
    val coupons: StateFlow<List<Coupon>>

    suspend fun loadCoupons()

    suspend fun orderCartItems(itemIds: List<Long>): Result<Unit, Error>
}
