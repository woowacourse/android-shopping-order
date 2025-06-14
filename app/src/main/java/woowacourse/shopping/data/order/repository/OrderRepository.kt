package woowacourse.shopping.data.order.repository

interface OrderRepository {
    suspend fun placeOrder(shoppingCartIds: List<Long>): Result<Unit>
}
