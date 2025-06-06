package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun addOrder(cartItemIds: List<String>): Result<Unit>
}
