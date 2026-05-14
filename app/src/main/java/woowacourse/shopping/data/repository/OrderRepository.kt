package woowacourse.shopping.data.repository

interface OrderRepository {
    suspend fun requestOrder(ids: List<Long>)
}