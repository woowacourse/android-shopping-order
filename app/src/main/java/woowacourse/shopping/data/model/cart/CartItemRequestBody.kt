package woowacourse.shopping.data.model.cart

data class CartItemRequestBody(
    val productId: Int,
    val quantity: Int,
)
