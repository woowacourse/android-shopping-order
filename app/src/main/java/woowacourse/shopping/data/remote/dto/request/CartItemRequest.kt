package woowacourse.shopping.data.remote.dto.request

data class CartItemRequest(
    val productId: Int,
    val quantity: Int
)