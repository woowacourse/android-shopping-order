package woowacourse.shopping.data.source.order

interface OrderDao {
    suspend fun orders(cartItemIds: List<String>)
}
