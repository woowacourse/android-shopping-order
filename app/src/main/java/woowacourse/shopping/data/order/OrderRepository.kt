package woowacourse.shopping.data.order

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Order

interface OrderRepository {
    fun loadOrders(callback: (List<Order>) -> Unit)
    fun loadOrder(orderId: Long, callback: (Order) -> Unit)
    fun orderCartProducts(cartProducts: List<CartProduct>, callback: () -> Unit)
}
