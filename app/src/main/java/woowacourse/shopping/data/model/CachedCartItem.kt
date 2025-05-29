package woowacourse.shopping.data.model

data class CachedCartItem(
    val cartId: Long,
    val productId: Long,
    val quantity: Int,
)
