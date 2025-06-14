package woowacourse.shopping.data.repository.order

interface OrderRepository {
    suspend fun placeOrder(cartIds: List<Long>)
}
