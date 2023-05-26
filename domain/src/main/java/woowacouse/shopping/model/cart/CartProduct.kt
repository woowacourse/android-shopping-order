package woowacouse.shopping.model.cart

import woowacouse.shopping.model.product.Product

data class CartProduct(
    val id: Long,
    val product: Product,
    val checked: Boolean
) {
    fun updateCartChecked(): CartProduct = copy(checked = !checked)
}
