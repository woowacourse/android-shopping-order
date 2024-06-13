package woowacourse.shopping.domain.model

data class CartWithProduct(
    val id: Long,
    val product: Product,
    val quantity: Quantity = Quantity(),
)
