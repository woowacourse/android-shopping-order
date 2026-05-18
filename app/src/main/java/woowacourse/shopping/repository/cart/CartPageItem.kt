package woowacourse.shopping.repository.cart

data class CartPageItem(
    val cartItemId: Long,
    val productId: Long,
    val quantity: Int,
)
