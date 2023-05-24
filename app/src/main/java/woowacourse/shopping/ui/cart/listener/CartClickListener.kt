package woowacourse.shopping.ui.cart.listener

import woowacourse.shopping.model.UiCartProduct

interface CartClickListener {
    fun onCountChanged(cartProduct: UiCartProduct, count: Int, isIncreased: Boolean)
    fun onCheckStateChanged(cartProduct: UiCartProduct, isChecked: Boolean)
    fun onDeleteClick(cartProduct: UiCartProduct)
}
