package woowacourse.shopping.presentation.view.cart.cartItem

import woowacourse.shopping.presentation.model.CartItemUiModel

interface CartItemEventHandler {
    fun onProductDeletion(cartItem: CartItemUiModel)

    fun onProductSelectionToggle(
        cartItem: CartItemUiModel,
        isChecked: Boolean,
    )
}
