package woowacourse.shopping.data.order.dto

data class OrderCartItem(
    val cartItemId: Long,
    val orderCartItemImageUrl: String,
    val orderCartItemName: String,
    val orderCartItemPrice: Int,
)
