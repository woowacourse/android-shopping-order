package woowacourse.shopping.remote

data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)
