package woowacourse.shopping.data.repository.order

import woowacourse.shopping.data.datasource.cart.CartDataSource
import woowacourse.shopping.data.datasource.order.OrderDataSource

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
    private val cartDataSource: CartDataSource,
) : OrderRepository {
    override suspend fun placeOrder(cartIds: List<Long>) {
        orderDataSource.submitOrder(cartIds)
        cartDataSource.removeCartItems(cartIds)
    }
}
