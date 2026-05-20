package woowacourse.shopping.data.repository.order

interface OrderRepository {
    suspend fun orders(cartItemIds: List<Long>)
}
