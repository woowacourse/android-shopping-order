package woowacourse.shopping.data.dto.cartitem

data class UpdateCartItemRequest(
    val productId: Long,
    val quantity: Int,
)
