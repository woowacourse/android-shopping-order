package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Order
import java.util.concurrent.CompletableFuture

interface OrderRepository {

    fun addOrder(
        basketIds: List<Int>,
        usingPoint: Int,
        totalPrice: Int,
    ): CompletableFuture<Result<Int>>

    fun getOrder(orderId: Int): CompletableFuture<Result<Order>>

    fun getOrders(
        onReceived: (orders: List<Order>) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )
}
