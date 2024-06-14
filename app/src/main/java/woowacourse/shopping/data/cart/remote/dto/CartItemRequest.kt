package woowacourse.shopping.data.cart.remote.dto

data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)
