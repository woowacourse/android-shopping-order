package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun order(cartItemIds: List<Long>)

    fun saveOrderItemTemp(cartItemId: Long, quantity: Int)

    fun loadOrderItemTemp(): Map<Long, Int>

    fun allOrderItemsTempQuantity(): Int

}