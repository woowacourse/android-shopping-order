package woowacourse.shopping.view.cart.selection

import woowacourse.shopping.view.cart.selection.adapter.CartProductViewHolder

interface CartProductSelectionEventHandler : CartProductViewHolder.EventHandler {
    fun onPreviousPageClick()

    fun onNextPageClick()

    fun onSelectAllClick()
}
