package woowacourse.shopping.presentation.cart.model

import woowacourse.shopping.presentation.common.model.ProductUiModel

data class CartItemUiModel(
    val product: ProductUiModel,
    val quantity: Int,
    val isSelected: Boolean,
) {
    val totalPrice: Long get() = product.price * quantity
}
