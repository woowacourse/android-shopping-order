package woowacourse.shopping.dto

import woowacourse.shopping.model.OrderHistoryItem

data class OrderHistoryItemDto(
    val productId: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int
) {
    fun toDomain() = OrderHistoryItem(
        productId = productId,
        name = name,
        price = price,
        imageUrl = imageUrl,
        quantity = quantity
    )
}
