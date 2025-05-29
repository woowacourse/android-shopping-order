package woowacourse.shopping.domain.model

data class CartItem(
    val cartId: Long = 0L,
    val product: Product,
    val quantity: Int,
) {
    val totalPrice: Int = product.price * quantity
}
