package woowacourse.shopping.domain.model

data class CartProduct(
    val product: Product,
    val quantity: Int,
) {
    val totalPrice: Int = product.price * quantity
}
