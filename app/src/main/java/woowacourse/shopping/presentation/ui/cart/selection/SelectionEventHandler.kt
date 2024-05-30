package woowacourse.shopping.presentation.ui.cart.selection

interface SelectionEventHandler {
    fun onCheckItem(itemId: Long)

    fun navigateToDetail(itemId: Long)

    fun deleteCartItem(itemId: Long)
}
