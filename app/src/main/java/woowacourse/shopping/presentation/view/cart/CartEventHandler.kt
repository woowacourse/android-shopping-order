package woowacourse.shopping.presentation.view.cart

interface CartEventHandler {
    fun onPlaceOrder()

    fun onBatchSelect(isChecked: Boolean)
}
