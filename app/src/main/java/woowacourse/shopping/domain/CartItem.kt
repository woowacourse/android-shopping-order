package woowacourse.shopping.domain

data class CartItem(
    val cartId: Long,
    val product: Product,
    val quantity: Int,
) {
    val totalPrice: Long
        get() = product.price.value * quantity
}
