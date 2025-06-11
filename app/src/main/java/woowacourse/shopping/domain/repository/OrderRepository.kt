package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun submitOrder(cartItemIds: List<Int>): Result<Unit>
}
