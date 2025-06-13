package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun createOrder(ids: List<Long>)
}
