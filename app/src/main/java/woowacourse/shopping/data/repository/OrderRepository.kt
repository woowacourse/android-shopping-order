package woowacourse.shopping.data.repository

interface OrderRepository {
    suspend fun placeOrder(cartIds: List<Long>)
}
