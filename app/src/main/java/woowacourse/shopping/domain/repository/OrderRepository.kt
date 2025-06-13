package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun createOrder(cartIds: List<Int>): Result<Unit>
}
