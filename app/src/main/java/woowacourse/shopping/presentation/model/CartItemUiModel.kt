package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.CartItem

data class CartItemUiModel(
    val cartItem: CartItem,
    val isSelected: Boolean = false,
)

fun CartItem.toCartItemUiModel(): CartItemUiModel =
    CartItemUiModel(
        cartItem = this,
    )
