package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun postOrderProducts(cartIds: List<Long>): Result<Unit>
}
