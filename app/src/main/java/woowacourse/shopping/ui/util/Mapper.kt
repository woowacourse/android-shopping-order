package woowacourse.shopping.ui.util

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.ui.cart.CartItemUiModel

fun List<CartItem>.toUiModel(
    selectedItems: Set<Int>,
    isAllSelected: Boolean,
): List<CartItemUiModel> =
    this.map { cartItem ->
        val isSelected = if (isAllSelected) true else selectedItems.contains(cartItem.id)
        cartItem.toUiModel(isSelected)
    }

fun CartItem.toUiModel(isSelected: Boolean): CartItemUiModel =
    CartItemUiModel(
        id = id,
        product = product,
        quantity = quantity.value,
        isSelected = isSelected,
        totalPrice = totalPrice,
    )
