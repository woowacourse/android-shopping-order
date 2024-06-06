package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun completeOrder(productIds: List<Long>): Result<Unit>
}
