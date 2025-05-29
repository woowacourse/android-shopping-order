package woowacourse.shopping.domain.model

data class Cart(
    val id: Long,
    val product: Product,
    var quantity: Int,
) {
    val totalPrice: Int get() = product.price * quantity
}
