package woowacourse.shopping.domain.model

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: Product,
)
