package woowacourse.shopping.ui.shopping

import woowacourse.shopping.model.product.Product

data class ShoppingProductUiState(
    val product: Product,
    val quantity: Int,
) {
    val isInCart: Boolean
        get() = quantity > 0
}
