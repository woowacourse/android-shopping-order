package woowacourse.shopping.presentation.ui.cart.selection

interface CartItemSelectionEventHandler {
    fun onCheckItem(cartItemId: Long)

    fun onProductClicked(productId: Long)

    fun onXButtonClicked(itemId: Long)
}
