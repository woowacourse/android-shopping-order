package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun createOrder(cartItemIds: List<Long>)
}
