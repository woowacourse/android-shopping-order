package woowacourse.shopping.remote.model

data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)
