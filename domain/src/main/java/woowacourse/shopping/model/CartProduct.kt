package woowacourse.shopping.model

data class CartProduct(
    val id: Int,
    val quantity: Int,
    val product: Product,
    val checked: Boolean = true,
)
