package woowacourse.shopping.domain.model

data class CartItem(
    val id: Int = 0,
    val product: Product,
    var quantity: Quantity,
) {
    fun totalPrice(): Int = product.price * quantity.count
}
