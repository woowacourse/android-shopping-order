package woowacourse.shopping.view.cart.select

import woowacourse.shopping.view.cart.select.adapter.CartProductViewHolder

interface CartProductSelectEventHandler : CartProductViewHolder.EventHandler {
    fun onPreviousPageClick()

    fun onNextPageClick()

    fun onSelectAllClick()
}
