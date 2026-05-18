package woowacourse.shopping.repository.query

data class CartPageItem(
    val cartItemId: Long,
    val productId: Long,
    val quantity: Int,
)
