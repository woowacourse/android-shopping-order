package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun requestOrder(itemIds: List<Long>)
}
