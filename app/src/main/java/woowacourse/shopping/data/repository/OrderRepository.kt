package woowacourse.shopping.data.repository

interface OrderRepository {
    suspend fun createOrder(cartItemIds: List<Long>): Result<Unit>
}
