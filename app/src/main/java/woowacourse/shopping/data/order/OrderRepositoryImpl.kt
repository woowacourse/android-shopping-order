package woowacourse.shopping.data.order

import woowacourse.shopping.data.entity.CartIdEntity
import woowacourse.shopping.data.entity.OrderRequest
import woowacourse.shopping.data.server.OrderRemoteDataSource
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(private val orderRemoteDataSource: OrderRemoteDataSource) : OrderRepository {
    override fun order(cart: Cart, points: Int, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        val order = OrderRequest(
            cartItemIds = cart.cartProducts.map { CartIdEntity(it.id) },
            originalPrice = cart.totalPrice,
            points = points
        )
        orderRemoteDataSource.addOrder(order, onSuccess, onFailure)
    }
}