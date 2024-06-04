package woowacourse.shopping.data.model

data class CartItemRequestBody(
    val productId: Int,
    val quantity: Int,
)
