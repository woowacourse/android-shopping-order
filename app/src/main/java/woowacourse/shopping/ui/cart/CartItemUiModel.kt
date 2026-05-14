package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.product.Product

data class CartItemUiModel(
    val id: Int,
    val product: Product,
    val quantity: Int,
    val totalPrice: Int,
    val isSelected: Boolean = false,
)
