package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun order(cartIds: List<Long>)
}
