package woowacourse.shopping.domain.model

data class Cart(
    val product: Product,
    var quantity: Int,
) {
    val totalPrice: Int get() = product.price * quantity
}
