package woowacourse.shopping.ui.cart.listener

import woowacourse.shopping.model.CartProductModel

interface CartClickListener {
    fun onCountChanged(cartProduct: CartProductModel, changedCount: Int)
    fun onCheckStateChanged(cartProduct: CartProductModel, isChecked: Boolean)
    fun onDeleteClick(cartProduct: CartProductModel)
}
