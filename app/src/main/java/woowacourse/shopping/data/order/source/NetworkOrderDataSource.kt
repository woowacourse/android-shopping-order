package woowacourse.shopping.data.order.source

import woowacourse.shopping.data.cart.CartItem

interface NetworkOrderDataSource {

    fun loadOrders(): Result<List<NetworkOrder>>

    fun loadOrder(orderId: Long): Result<NetworkOrder>

    fun saveOrder(cartItems: List<CartItem>): Result<Long>
}
