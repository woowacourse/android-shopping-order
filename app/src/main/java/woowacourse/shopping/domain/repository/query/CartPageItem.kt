package woowacourse.shopping.domain.repository.query

data class CartPageItem(
    val cartItemId: Long,
    val productId: Long,
    val quantity: Int,
)
