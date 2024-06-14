package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun postOrder(cartItemIds: List<Int>): Result<Unit>
}
