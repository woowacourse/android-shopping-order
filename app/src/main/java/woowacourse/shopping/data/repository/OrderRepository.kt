package woowacourse.shopping.data.repository

interface OrderRepository {
    suspend fun insertOrders(cartItemIds: List<Long>): Result<Unit>
}
