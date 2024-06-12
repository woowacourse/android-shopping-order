package woowacourse.shopping.presentation.ui.cart.selection

interface SelectionEventHandler {
    fun onItemClicked(itemId: Long)

    fun deleteCartItem(itemId: Long)
}
