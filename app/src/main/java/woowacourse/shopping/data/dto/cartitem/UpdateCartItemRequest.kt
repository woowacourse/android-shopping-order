package woowacourse.shopping.data.dto.cartitem

data class UpdateCartItemRequest(
    val productId: Int,
    val quantity: Int,
)
