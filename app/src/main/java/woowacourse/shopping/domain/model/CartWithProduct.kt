package woowacourse.shopping.domain.model

class CartWithProduct(
    val id: Long,
    val product: Product,
    val quantity: Quantity = Quantity(),
)
