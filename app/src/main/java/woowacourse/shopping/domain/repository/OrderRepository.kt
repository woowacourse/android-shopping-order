package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun insertOrderByIds(cartItemIds: List<Int>): Result<Unit>
}
