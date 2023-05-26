package woowacouse.shopping.model

data class CartProduct(
    val id: Long,
    val product: Product,
    var checked: Boolean
)
