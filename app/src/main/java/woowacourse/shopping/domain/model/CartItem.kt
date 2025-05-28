package woowacourse.shopping.domain.model

data class CartItem(
    val cartId: Long,
    val product: Product,
    val amount: Int,
) {
    val totalPrice: Int
        get() = product.price.value * amount
}
