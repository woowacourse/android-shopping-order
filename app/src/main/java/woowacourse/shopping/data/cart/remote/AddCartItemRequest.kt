package woowacourse.shopping.data.cart.remote

data class AddCartItemRequest(
    val productId: Int,
    val quantity: Int,
)
