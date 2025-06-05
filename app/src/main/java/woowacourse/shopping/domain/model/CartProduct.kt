package woowacourse.shopping.domain.model

data class CartProduct(
    val cartId: Long,
    val product: Product,
    val quantity: Int,
) {
    val totalPrice: Int = product.price * quantity
}
