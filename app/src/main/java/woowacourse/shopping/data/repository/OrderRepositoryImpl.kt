package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.model.request.OrderRequest
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun order(cartItems: List<CartItem>): Result<Unit> {
        val request = OrderRequest(cartItems.map { it.cartId })
        return orderDataSource.postOrder(request)
    }
}
