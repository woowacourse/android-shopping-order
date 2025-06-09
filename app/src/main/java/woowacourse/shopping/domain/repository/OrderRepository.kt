package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun orderProducts(ids: List<Long>): Result<Unit>
}