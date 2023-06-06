package woowacourse.shopping.data.order

import woowacourse.shopping.data.entity.CartIdEntity
import woowacourse.shopping.data.entity.PayRequest
import woowacourse.shopping.data.server.OrderRemoteDataSource
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(private val orderRemoteDataSource: OrderRemoteDataSource) : OrderRepository {
    override fun order(cart: Cart, points: Int, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        val order = PayRequest(
            cartItemIds = cart.cartProducts.map { CartIdEntity(it.id) },
            originalPrice = cart.totalPrice,
            points = points
        )
        orderRemoteDataSource.addOrder(order, onSuccess, onFailure)
    }
}