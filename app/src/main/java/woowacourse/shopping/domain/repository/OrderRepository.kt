package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun completeOrder(cartItemIds: List<Long>): Result<Unit>
}
