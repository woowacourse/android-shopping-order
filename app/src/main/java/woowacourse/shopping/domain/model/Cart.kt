package woowacourse.shopping.domain.model

data class Cart(
    val id: Long,
    val product: Product,
    val quantity: Int,
    val isChecked: Boolean = false,
) {
    val totalPrice: Int get() = product.price * quantity
}
