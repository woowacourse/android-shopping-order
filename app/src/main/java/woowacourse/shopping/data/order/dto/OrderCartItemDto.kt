package woowacourse.shopping.data.order.dto

data class OrderCartItemDto(
    val cartItemId: Long,
    val orderCartItemName: String,
    val orderCartItemPrice: Int,
    val orderCartItemImageUrl: String,
)
