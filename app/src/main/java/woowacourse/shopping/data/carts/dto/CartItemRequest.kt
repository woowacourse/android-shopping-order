package woowacourse.shopping.data.carts.dto

data class CartItemRequest(
    val productId: Int,
    val quantity: Int
)