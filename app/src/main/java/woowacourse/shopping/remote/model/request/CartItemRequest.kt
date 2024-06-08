package woowacourse.shopping.remote.model.request

data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)
