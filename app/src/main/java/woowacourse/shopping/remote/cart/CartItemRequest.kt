package woowacourse.shopping.remote.cart

data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)
