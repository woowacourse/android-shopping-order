package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun order(cartItemIds: List<Long>)

    fun saveOrderItem(cartItemId: Long, quantity: Int)

    fun orderItems(): Map<Long, Int>

    fun allOrderItemsQuantity(): Int

    fun orderItemsTotalPrice(): Int

}