package woowacourse.shopping.data.remote.request

data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)
