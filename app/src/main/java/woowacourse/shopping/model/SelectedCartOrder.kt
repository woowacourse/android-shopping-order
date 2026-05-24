package woowacourse.shopping.model

import kotlinx.serialization.Serializable

@Serializable
data class SelectedCartOrder(
    val items: List<SelectedCartOrderItem>,
) {
    fun totalOrderAmount(): Long = items.sumOf(SelectedCartOrderItem::totalPrice)

    fun hasItemQuantityAtLeast(quantity: Int): Boolean = items.any { item -> item.quantity >= quantity }

    fun highestPricedItemAmountWithQuantityAtLeast(quantity: Int): Long =
        items
            .filter { item -> item.quantity >= quantity }
            .maxOfOrNull { item -> item.price.toLong() }
            ?: 0
}

@Serializable
data class SelectedCartOrderItem(
    val cartItemId: Long,
    val productId: Long,
    val price: Int,
    val quantity: Int,
) {
    fun totalPrice(): Long = price.toLong() * quantity
}
