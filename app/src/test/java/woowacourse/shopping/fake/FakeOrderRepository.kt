package woowacourse.shopping.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.domain.model.order.Coupon
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.error.Error
import woowacourse.shopping.error.NetworkError
import woowacourse.shopping.error.Result

class FakeOrderRepository(
    initialCoupons: List<Coupon> = emptyList(),
) : OrderRepository {
    private val _coupons = MutableStateFlow(initialCoupons)
    override val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

    override suspend fun loadCoupons() = Result.Success<Unit, NetworkError>(Unit)

    override suspend fun orderCartItems(itemIds: List<Long>): Result<Unit, Error> = Result.Success(Unit)
}
