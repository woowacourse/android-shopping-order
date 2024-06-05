package woowacourse.shopping.presentation.ui.cart

import woowacourse.shopping.presentation.ui.QuantityHandler

interface CartHandler : QuantityHandler {
    fun onDeleteClick(cartId: Long)

    fun onCheckBoxClicked(cartId: Long)

    fun onTotalCheckBoxClicked(isChecked: Boolean)

    fun onOrderClicked()

    fun onPlusButtonClick(productId: Long)
}
