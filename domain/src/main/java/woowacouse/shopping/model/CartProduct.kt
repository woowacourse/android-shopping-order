package woowacouse.shopping.model

data class CartProduct(
    val id: Long,
    val product: Product,
    val checked: Boolean
) {
    fun updateCartChecked(): CartProduct = copy(checked = !checked)
}
