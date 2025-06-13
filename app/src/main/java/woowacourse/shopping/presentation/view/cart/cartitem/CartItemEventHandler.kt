package woowacourse.shopping.presentation.view.cart.cartitem

import woowacourse.shopping.presentation.model.CartItemUiModel

interface CartItemEventHandler {
    fun onProductDeletion(cartItem: CartItemUiModel)

    fun onProductSelectionToggle(cartItem: CartItemUiModel)
}
