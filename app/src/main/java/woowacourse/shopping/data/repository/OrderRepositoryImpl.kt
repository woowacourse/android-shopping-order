package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.datasource.OrderDataSource

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : OrderRepository {
    override suspend fun placeOrder(cartIds: List<Long>) {
        orderDataSource.submitOrder(cartIds)
        cartItemDataSource.removeCartItems(cartIds)
    }
}
