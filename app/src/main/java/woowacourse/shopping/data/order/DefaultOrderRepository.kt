package woowacourse.shopping.data.order

import woowacourse.shopping.data.dto.CartId
import woowacourse.shopping.data.order.request.PostOrderRequest
import woowacourse.shopping.data.server.OrderRemoteDataSource
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(private val orderRemoteDataSource: OrderRemoteDataSource) : OrderRepository {
    override fun order(cart: Cart, points: Int, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        val order = PostOrderRequest(
            cartItemIds = cart.cartProducts.map { CartId(it.id) },
            originalPrice = cart.totalPrice,
            points = points
        )
        orderRemoteDataSource.addOrder(order, onSuccess, onFailure)
    }
}