package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product

class FakeOrderRepository : OrderRepository {
    override suspend fun orders(cartItemIds: List<Long>) {
        TODO("Not yet implemented")
    }

}
