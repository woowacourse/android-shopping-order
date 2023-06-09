package woowacourse.shopping.data.order

import woowacourse.shopping.data.cart.CartItem
import java.util.concurrent.CompletableFuture

interface OrderRepository {

    fun getOrders(): CompletableFuture<Result<List<Order>>>

    fun getOrder(orderId: Long): CompletableFuture<Result<Order>>

    fun createOrder(cartItems: List<CartItem>): CompletableFuture<Result<Long>>
}
